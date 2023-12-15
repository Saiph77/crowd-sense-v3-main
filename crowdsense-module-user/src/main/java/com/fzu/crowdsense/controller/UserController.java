package com.fzu.crowdsense.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.crypto.asymmetric.RSA;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.model.dto.UserDTO;
import com.fzu.crowdsense.model.entity.User;
import com.fzu.crowdsense.model.request.user.*;
import com.fzu.crowdsense.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

import static com.fzu.crowdsense.constant.RedisConstants.USER_INFO;


/**
 * 用户接口
 *
 * @author yupi
 */
@Api
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private RSA rsa;


    /**
     * 获取RSA公钥
     *
     * @return RSA公钥
     */
    @GetMapping("/rsa")
    public BaseResponse<String> getPublicKey() {
        return ResultUtils.success(rsa.getPublicKeyBase64());
    }


    /**
     * 用户注册
     *
     * @param userRegisterRequest userRegisterRequest
     * @return userid
     */
    @PostMapping("/register")
    public BaseResponse<String> userRegister(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

        User user = userService.userRegister(userRegisterRequest);
        StpUtil.login(user.getId());

        return ResultUtils.success(StpUtil.getTokenValue());
    }


    /**
     * 通过手机号和密码登录
     *
     * @param loginRequest 用户密码登录结构体
     * @return authentication
     */
    @PostMapping("/login/by/password")
    public BaseResponse<String> loginByPassword(@RequestBody @Valid PasswordLoginRequest loginRequest) {
        User user = userService.loginByPassword(loginRequest);

        StpUtil.login(user.getId());


        return ResultUtils.success(StpUtil.getTokenValue());
    }


    /**
     * 通过手机号和手机验证码登录
     *
     * @param loginRequest 用户验证码登录结构体
     * @return authentication
     */
    @PostMapping("/login/by/code")
    public BaseResponse<String> loginByVerifyCode(@RequestBody @Valid VerifyCodeLoginRequest loginRequest) {

        User user = userService.loginByVerifyCode(loginRequest);


        StpUtil.login(user.getId());


        return ResultUtils.success(StpUtil.getTokenValue());
    }


    /**
     * 登出
     *
     * @param request request
     * @return logout
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = Long.parseLong((String) StpUtil.getLoginId());
        // 删除缓存
        stringRedisTemplate.delete(USER_INFO + id);


        StpUtil.logout();


        return ResultUtils.success(null);
    }


    /**
     * 获取当前用户信息
     *
     * @return BaseResponse
     */
    @PostMapping("/current")
    public BaseResponse<UserDTO> getCurrentUser() {
        Long id = Long.valueOf((String) StpUtil.getLoginId());
        // 获取经过脱敏处理后的userInfo
        User user = userService.getUserInfoById(id);

        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        userDTO.setPhone(DesensitizedUtil.mobilePhone(userDTO.getPhone()));

        return ResultUtils.success(userDTO);
    }


    /**
     * 更新用户密码
     *
     * @return success
     */
    @PutMapping("/update/password")
    public BaseResponse<String> updateUserPassword(@RequestBody @Valid UpdateUserPasswordRequest userPasswordRequest) {


        userService.updatePassword(userPasswordRequest);

        // 删除缓存
        stringRedisTemplate.delete(USER_INFO + userPasswordRequest.getUserId());

        StpUtil.logout();

        return ResultUtils.success(null);
    }

    /**
     * 更新用户手机号
     *
     * @return success
     */
    @PutMapping("/update/phone")
    public BaseResponse<String> updateUserPhone(@RequestBody @Valid UpdateUserPhoneRequest updateUserPhoneRequest) {

        userService.updatePhone(updateUserPhoneRequest);

        // 删除缓存
        stringRedisTemplate.delete(USER_INFO + updateUserPhoneRequest.getUserId());

        StpUtil.logout();

        return ResultUtils.success(null);
    }


    /**
     * 更新用户个人信息
     *
     * @param infoRequest infoRequest
     * @return UserDTO
     */
    @PutMapping("/update/info")
    public BaseResponse<String> updateUserInfo(@RequestBody @Valid UpdateUserInfoRequest infoRequest) {
        Long userId = infoRequest.getUserId();
        Long id = Long.valueOf((String) StpUtil.getLoginId());
        if (!Objects.equals(id, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id错误");

        }

        String nickName = infoRequest.getNickName();
        String signature = infoRequest.getSignature();
        String address = infoRequest.getAddress();
        Double latitude = infoRequest.getLatitude();
        Double longitude = infoRequest.getLongitude();
        String home = infoRequest.getHome();

        userService.updateUserInfo(userId, nickName, signature, address, latitude, longitude,home);


        return ResultUtils.success(null);
    }


    /**
     * 更新用户头像功能
     *
     * @param icon icon
     * @return url
     */
    @PostMapping("/update/icon/{userId}")
    public BaseResponse<String> uploadUserIcon(@PathVariable("userId") Long userId, MultipartFile icon) {
        Long id = Long.valueOf((String) StpUtil.getLoginId());
        if (!Objects.equals(id, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id错误");

        }
        String path = userService.updateIcon(userId, icon);

        return ResultUtils.success(path);
    }

    /**
     * 根据id查询昵称
     *
     * @return success
     */
    @GetMapping("/get/publisherName")
    public BaseResponse<String> getNameById(String publisherId){
        return userService.getNameById( Long.parseLong(publisherId));
    }

}
