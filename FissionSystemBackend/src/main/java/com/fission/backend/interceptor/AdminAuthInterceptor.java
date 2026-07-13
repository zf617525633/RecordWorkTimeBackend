package com.fission.backend.interceptor;

import com.fission.backend.common.AdminContext;
import com.fission.backend.common.Result;
import com.fission.backend.entity.SysAdmin;
import com.fission.backend.mapper.SysAdminMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private SysAdminMapper sysAdminMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String adminIdStr = redisTemplate.opsForValue().get("admin_token:" + token);
            
            if (adminIdStr != null) {
                // 查询Admin信息存入ThreadLocal
                SysAdmin admin = sysAdminMapper.selectById(Long.parseLong(adminIdStr));
                if (admin != null && admin.getStatus() == 1) {
                    AdminContext.set(admin);
                    return true; // 鉴权通过
                }
            }
        }

        // 鉴权失败
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AdminContext.remove();
    }
}
