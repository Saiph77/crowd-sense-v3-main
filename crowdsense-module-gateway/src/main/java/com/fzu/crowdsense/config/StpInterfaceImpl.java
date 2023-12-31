package com.fzu.crowdsense.config;


import cn.dev33.satoken.stp.StpInterface;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.fzu.crowdsense.constant.RedisConstants.USER_ROLE;


/**
 * <p>
 * 自定义权限验证接口扩展
 * <p>
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-03-18
 **/
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 返回此 loginId 拥有的权限列表
     *
     * @param loginId   用户id
     * @param loginType loginType
     * @return 权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 返回此 loginId 拥有的权限列表
        return null;
    }

    /**
     * 返回此 loginId 拥有的角色列表
     *
     * @param loginId   用户id
     * @param loginType loginType
     * @return 角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();

        // 从redis中获取角色信息
        String role = stringRedisTemplate.opsForValue().get(USER_ROLE + loginId);

        list.add(role);

        return list;
    }

}
