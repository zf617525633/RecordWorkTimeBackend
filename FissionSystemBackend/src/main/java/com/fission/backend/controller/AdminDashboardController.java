package com.fission.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fission.backend.common.Result;
import com.fission.backend.entity.FissionUser;
import com.fission.backend.entity.FissionWithdraw;
import com.fission.backend.entity.FissionPointLog;
import com.fission.backend.entity.SysAdmin;
import com.fission.backend.mapper.FissionUserMapper;
import com.fission.backend.mapper.FissionWithdrawMapper;
import com.fission.backend.mapper.FissionPointLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private FissionUserMapper userMapper;
    
    @Autowired
    private FissionWithdrawMapper withdrawMapper;
    
    @Autowired
    private FissionPointLogMapper pointLogMapper;

    /**
     * B端后台：首页数据看板聚合接口
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats(@RequestParam(required = false) String appId) {
        Map<String, Object> data = new HashMap<>();
        
        // 数据隔离逻辑：普通管理员只能看自己的 appId
        SysAdmin currentAdmin = com.fission.backend.common.AdminContext.get();
        if (currentAdmin != null && currentAdmin.getRole() == 2) {
            appId = currentAdmin.getAppId();
        }
        
        // 1. 总注册用户数
        QueryWrapper<FissionUser> userQw = new QueryWrapper<>();
        if (appId != null && !appId.isEmpty()) userQw.eq("app_id", appId);
        long totalUsers = userMapper.selectCount(userQw);
        data.put("totalUsers", totalUsers);
        
        // 2. 今日新增用户
        String todayStart = LocalDate.now().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        QueryWrapper<FissionUser> todayUserQw = new QueryWrapper<>();
        if (appId != null && !appId.isEmpty()) todayUserQw.eq("app_id", appId);
        todayUserQw.ge("create_time", todayStart);
        long todayNewUsers = userMapper.selectCount(todayUserQw);
        data.put("todayNewUsers", todayNewUsers);
        
        // 3. 历史发放总积分 (签到+邀请奖励)
        QueryWrapper<FissionPointLog> pointsQw = new QueryWrapper<>();
        if (appId != null && !appId.isEmpty()) pointsQw.eq("app_id", appId);
        pointsQw.in("change_type", 1, 2);
        List<FissionPointLog> pointLogs = pointLogMapper.selectList(pointsQw);
        long totalPointsIssued = pointLogs.stream().mapToInt(FissionPointLog::getPoints).sum();
        data.put("totalPointsIssued", totalPointsIssued);
        
        // 4. 累计打款金额 (提现成功 status = 2)
        QueryWrapper<FissionWithdraw> withdrawQw = new QueryWrapper<>();
        if (appId != null && !appId.isEmpty()) withdrawQw.eq("app_id", appId);
        withdrawQw.eq("status", 2);
        List<FissionWithdraw> withdraws = withdrawMapper.selectList(withdrawQw);
        double totalWithdrawAmount = withdraws.stream().mapToDouble(FissionWithdraw::getAmount).sum();
        data.put("totalWithdrawAmount", totalWithdrawAmount);
        
        // 5. 来源渠道分布 (用于ECharts饼图)
        QueryWrapper<FissionUser> channelQw = new QueryWrapper<>();
        if (appId != null && !appId.isEmpty()) channelQw.eq("app_id", appId);
        channelQw.select("source_channel, count(1) as status"); // 借用 status 字段存储 count 避免自定义对象
        channelQw.groupBy("source_channel");
        
        List<Map<String, Object>> channelCounts = userMapper.selectMaps(channelQw);
        Map<String, Long> sourceChannels = new HashMap<>();
        for (Map<String, Object> map : channelCounts) {
            String channel = (String) map.get("source_channel");
            Long count = ((Number) map.get("status")).longValue(); // mybatis-plus 自动映射
            if (channel == null || channel.isEmpty()) channel = "organic";
            sourceChannels.put(channel, sourceChannels.getOrDefault(channel, 0L) + count);
        }
        
        // 如果没有数据，给一个默认空集防止前端报错
        if (sourceChannels.isEmpty()) {
             sourceChannels.put("暂无数据", 0L);
        }
        
        data.put("sourceChannels", sourceChannels);

        return Result.success(data);
    }
}

