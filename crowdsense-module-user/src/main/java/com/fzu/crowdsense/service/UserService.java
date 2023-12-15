package com.fzu.crowdsense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.model.entity.User;
import com.fzu.crowdsense.model.request.user.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author yupi
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userRegisterRequest userRegisterRequest
     * @return id
     */
    User userRegister(UserRegisterRequest userRegisterRequest);


    /**
     * 通过验证码登录
     *
     * @param loginRequest 用户验证码登录结构体
     * @return user对象
     */
    User loginByVerifyCode(VerifyCodeLoginRequest loginRequest);


    /**
     * 通过密码登录
     *
     * @param loginRequest 用户密码登录结构体
     * @return User
     */
    User loginByPassword(PasswordLoginRequest loginRequest);


    /**
     * 根据id获取用户信息
     *
     * @param id id
     * @return userDTO
     */
    User getUserInfoById(Long id);


    /**
     * 更新用户密码
     *
     * @param userPasswordRequest userPasswordRequest
     */
    void updatePassword(UpdateUserPasswordRequest userPasswordRequest);


    /**
     * 更新用户手机号
     *
     * @param updateUserPhoneRequest updateUserPhoneRequest
     */
    void updatePhone(UpdateUserPhoneRequest updateUserPhoneRequest);

    /**
     * 更新用户个人信息
     *
     * @param userId    用户id
     * @param nickName  昵称
     * @param signature 签名
     * @param address   地址
     * @param latitude  纬度
     * @param longitude 精度
     * @param home      用户自定义首页
     */
    void updateUserInfo(Long userId, String nickName, String signature, String address, Double latitude, Double longitude, String home);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 匹配 验证码和手机号
     *
     * @param phone phone
     * @param code  验证码
     * @param key   redis存储对应验证码的前缀
     * @return 是否匹配
     */
    Boolean verifyPhone(String phone, String code, String key);

    /**
     * 更新icon
     *
     * @param icon   icon
     * @param userId 用户id
     * @return url
     */
    String updateIcon(Long userId, MultipartFile icon);

    /**
     * 根据id查询昵称
     *
     * @return success
     */
    BaseResponse<String> getNameById(Long publisherId);


}
