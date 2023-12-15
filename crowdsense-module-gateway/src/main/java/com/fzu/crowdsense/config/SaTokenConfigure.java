package com.fzu.crowdsense.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.fzu.crowdsense.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fzu.crowdsense.common.ErrorCode.PARAMS_ERROR;
import static com.fzu.crowdsense.constant.RoleConstants.ROLE_ADMIN;
import static com.fzu.crowdsense.constant.RoleConstants.ROLE_USER;

/**
 * <p>
 * 注册 Sa-Token全局过滤器
 * <p>
 *
 * @author Zaki
 * @version 2.0
 * @since 2023-03-18
 **/
@Configuration
@Slf4j
public class SaTokenConfigure {
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    // 拦截全部path
                // 开放地址
                .addExclude("/user/login/**",
                        "/user/register",
                        "/user/rsa",
                        "/task/publish/count/smallTask",
                        "/task/publish/get",
                        "/task/publish/list",
                        "/task/publish/list/page",
                        "/task/publish/list/publish/bigTask",
                        "/task/publish/list/page/publish/smallTask",
                        "/type/select",
                        "/sms/send"
                       )
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由，并排除 用于开放登录
                    SaRouter.match("/**", r -> StpUtil.checkLogin());

                    // 角色认证 -- 不同模块, 校验不同权限3
                    SaRouter.match("/user/**", r -> StpUtil.checkRole(ROLE_USER));
                    SaRouter.match("/admin/**", r -> StpUtil.checkRole(ROLE_ADMIN));

                    // 更多匹配 ...  */
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    log.warn(e.getMessage());

                    return JacksonUtils.toJson(ResultUtils.error(PARAMS_ERROR, PARAMS_ERROR.getMessage(), e.getMessage()));
                });
    }
}
