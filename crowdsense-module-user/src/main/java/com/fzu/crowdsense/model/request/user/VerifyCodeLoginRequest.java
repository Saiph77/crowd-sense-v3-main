package com.fzu.crowdsense.model.request.user;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * <p>
 * 用户登录结构体
 * <p>
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-03-18
 **/

@Data
public class VerifyCodeLoginRequest implements Serializable {


    private static final long serialVersionUID = 3191241716373120793L;


    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "手机号格式错误")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    @Pattern(regexp = "^[0-9]{6}$", message = "验证码错误")
    private String verifyCode;
}
