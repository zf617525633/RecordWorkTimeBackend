package com.fission.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fission.backend.common.Result;
import com.fission.backend.entity.FissionAccount;
import com.fission.backend.entity.FissionUser;
import com.fission.backend.mapper.FissionAccountMapper;
import com.fission.backend.mapper.FissionUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private FissionUserMapper userMapper;

    @Autowired
    private FissionAccountMapper accountMapper;

    /**
     * 获取 H5 端当前用户的基本信息及积分资产
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestParam Long userId) {
        FissionUser user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        FissionAccount account = accountMapper.selectById(userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("inviteCode", user.getInviteCode());
        
        if (account != null) {
            data.put("availablePoints", account.getAvailablePoints());
            data.put("totalPoints", account.getTotalPoints());
            data.put("frozenPoints", account.getFrozenPoints());
        } else {
            data.put("availablePoints", 0);
            data.put("totalPoints", 0);
            data.put("frozenPoints", 0);
        }

        return Result.success(data);
    }
}
