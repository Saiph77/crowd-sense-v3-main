package com.fzu.crowdsense.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.mapper.UserMapper;
import com.fzu.crowdsense.model.entity.User;
import com.fzu.crowdsense.model.request.user.*;
import com.fzu.crowdsense.service.UserService;
import com.fzu.crowdsense.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.fzu.crowdsense.common.ErrorCode.*;
import static com.fzu.crowdsense.constant.RedisConstants.*;
import static com.fzu.crowdsense.constant.RoleConstants.DEFAULT_NICK_NAME_PREFIX;
import static com.fzu.crowdsense.constant.RoleConstants.ROLE_USER;
import static com.fzu.crowdsense.constant.SaTokenConstants.SESSION_USER;
import static com.fzu.crowdsense.constant.SystemConstants.FILE_ICON_SUB_PATH;
import static com.fzu.crowdsense.constant.UserConstant.ADMIN_ROLE;
import static com.fzu.crowdsense.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author yupi
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RSA rsa;

    @Resource
    private Snowflake snowflake;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 实现用户注册
     *
     * @param userRegisterRequest userRegisterRequest
     * @return userId
     */
    @Override
    public User userRegister(UserRegisterRequest userRegisterRequest) {
        String password = userRegisterRequest.getPassword();
        String phone = userRegisterRequest.getPhone();
        String code = userRegisterRequest.getVerifyCode();

        try {
            password = rsa.decryptStr(password, KeyType.PrivateKey);
        } catch (Exception e) {
            throw new BusinessException(PARAMS_ERROR, "密码未加密");
        }


        // 验证 手机号和验证码是否匹配
        if (!verifyPhone(phone, code, REGISTER_CODE_KEY)) {
            throw new BusinessException(PARAMS_ERROR, "验证码错误");
        }


        // 手机号不能重复
        User one = userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone));

        if (BeanUtil.isNotEmpty(one)) {
            throw new BusinessException(OPERATION_ERROR, "该手机号已经注册");
        }

        // 生成用户id
        Long id = snowflake.nextId();

        // 加密
        String newPassword = SmUtil.sm3(id + password);

        // 插入数据
        User user = new User();
        user.setId(id);
        user.setPhone(phone);
        user.setPassword(newPassword);
        user.setRole(ROLE_USER);
        user.setCreateTime(LocalDateTime.now());
        user.setIsDelete(0);
        user.setNickName(DEFAULT_NICK_NAME_PREFIX + RandomUtil.randomString(10));

        boolean b = save(user);
        if (!b) {
            throw new BusinessException(OPERATION_ERROR, "注册失败");
        }

        // 保存用户角色到redis
        stringRedisTemplate.opsForValue().set(USER_ROLE + user.getId(), user.getRole(), USER_ROLE_TTL, TimeUnit.HOURS);

        return user;
    }


    /**
     * 实现通过验证码登录
     *
     * @param loginRequest 用户验证码登录结构体
     * @return UserDTO
     */
    @Override
    public User loginByVerifyCode(VerifyCodeLoginRequest loginRequest) {
        String phone = loginRequest.getPhone();

        if (!verifyPhone(phone, loginRequest.getVerifyCode(), LOGIN_CODE_KEY)) {
            // 验证码错误
            throw new BusinessException(PARAMS_ERROR, "验证码错误");
        }

        // 一致，根据手机号查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone));


        if (user == null) {
            // 用户不存在
            throw new BusinessException(PARAMS_ERROR, "用户尚未注册");
        }

        stringRedisTemplate.opsForValue().set(USER_ROLE + user.getId(), user.getRole(), USER_ROLE_TTL, TimeUnit.HOURS);

        return user;
    }


    /**
     * 实现通过密码登录
     *
     * @param loginRequest 用户密码登录结构体
     * @return User
     */
    @Override
    public User loginByPassword(PasswordLoginRequest loginRequest) {
        String phone = loginRequest.getPhone();
        String password;
        try {
            password = rsa.decryptStr(loginRequest.getPassword(), KeyType.PrivateKey);
        } catch (Exception e) {
            throw new BusinessException(PARAMS_ERROR, "密码未加密");
        }


        // 根据手机号查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone));

        if (user == null) {
            throw new BusinessException(PARAMS_ERROR, "用户尚未注册");
        }

        // 验证密码
        password = SmUtil.sm3(user.getId() + password);
        if (!StrUtil.equals(password, user.getPassword())) {
            throw new BusinessException(PARAMS_ERROR, "密码错误");
        }

        stringRedisTemplate.opsForValue().set(USER_ROLE + user.getId(), user.getRole(), USER_ROLE_TTL, TimeUnit.HOURS);

        return user;
    }

    /**
     * 实现根据用户id获取用户基本信息
     *
     * @param id id
     * @return UserDTO
     */
    @Override
    public User getUserInfoById(Long id) {

        String key = USER_INFO + id;
        // 先查询redis
        String baseInfo = stringRedisTemplate.opsForValue().get(key);

        // 有则直接返回并刷新过期时间
        if (StrUtil.isNotEmpty(baseInfo)) {
            stringRedisTemplate.expire(key, USER_INFO_TTL, TimeUnit.MINUTES);
            return JSONUtil.toBean(baseInfo, User.class);
        }

        // 没有则查询数据库并写入redis
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("id", id));
        if (BeanUtil.isEmpty(user)) {
            throw new BusinessException(PARAMS_ERROR, "该用户不存在");
        }

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(user), USER_INFO_TTL, TimeUnit.MINUTES);

        return user;
    }

    /**
     * 实现更新用户密码
     *
     * @param userPasswordRequest userPasswordRequest
     */
    @Override
    public void updatePassword(UpdateUserPasswordRequest userPasswordRequest) {
        String phone = userPasswordRequest.getPhone();
        Long id = userPasswordRequest.getUserId();

        if (!verifyPhone(phone, userPasswordRequest.getVerifyCode(), UPDATE_CODE_KEY)) {
            // 验证码错误
            throw new BusinessException(OPERATION_ERROR, "验证码错误");
        }


        // 一致，根据id查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("id", id));

        if (BeanUtil.isEmpty(user)) {
            throw new BusinessException(OPERATION_ERROR, "该用户不存在");
        }

        // 验证phone是否一致
        if (!StrUtil.equals(user.getPhone(), phone)) {
            throw new BusinessException(OPERATION_ERROR, "该手机号不属于该账号");
        }


        // 密码解密/加密
        String newPassword;
        try {
            newPassword = rsa.decryptStr(userPasswordRequest.getNewPassword(), KeyType.PrivateKey);
        } catch (Exception e) {
            throw new BusinessException(PARAMS_ERROR, "密码未加密");
        }


        newPassword = SmUtil.sm3(userPasswordRequest.getUserId() + newPassword);

        if (StrUtil.equals(user.getPassword(), newPassword)) {
            // 如果新旧密码一致，则返回错误结果
            throw new BusinessException(OPERATION_ERROR, "新旧密码不能一致");
        }


        boolean b = update(new UpdateWrapper<User>()
                .set("password", newPassword)
                .set("update_time", LocalDateTime.now())
                .eq("id", id));


        if (!b) {
            log.error("更新用户密码{}失败", userPasswordRequest.getUserId());
            throw new BusinessException(OPERATION_ERROR, "内部错误-更新失败");
        }
    }

    /**
     * 实现更新用户手机号
     *
     * @param updateUserPhoneRequest updateUserPhoneRequest
     */
    @Override
    public void updatePhone(UpdateUserPhoneRequest updateUserPhoneRequest) {
        Long id = updateUserPhoneRequest.getUserId();
        String oldPhone = updateUserPhoneRequest.getOldPhone();
        String newPhone = updateUserPhoneRequest.getNewPhone();

        // 验证新旧手机号的验证码
        if (!verifyPhone(oldPhone, updateUserPhoneRequest.getOldCode(), UPDATE_CODE_KEY)) {
            throw new BusinessException(OPERATION_ERROR, oldPhone + "-验证码错误");
        }
        if (!verifyPhone(newPhone, updateUserPhoneRequest.getNewCode(), UPDATE_CODE_KEY)) {
            throw new BusinessException(OPERATION_ERROR, newPhone + "-验证码错误");
        }

        // 不允许新旧手机号相同
        if (StrUtil.equals(oldPhone, newPhone)) {
            throw new BusinessException(OPERATION_ERROR, oldPhone + "新旧手机号不允许一致");
        }


        User user = userMapper.selectOne(new QueryWrapper<User>().eq("id", id));

        if (BeanUtil.isEmpty(user)) {
            throw new BusinessException(OPERATION_ERROR, "该用户不存在");
        }

        // 验证手机号是否一致
        if (!StrUtil.equals(user.getPhone(), oldPhone)) {
            throw new BusinessException(OPERATION_ERROR, "该手机号不属于该账号");
        }

        boolean b = update(new UpdateWrapper<User>()
                .set("phone", newPhone)
                .set("update_time", LocalDateTime.now())
                .eq("id", id));

        if (!b) {
            log.error("更新用户{}失败", user.getId());
            throw new BusinessException(OPERATION_ERROR, "内部错误-更新失败");
        }

    }


    /**
     * 实现更新用户个人信息
     *
     * @param userId    用户id
     * @param nickName  昵称
     * @param signature 签名
     * @param address   地址
     * @param latitude  纬度
     * @param longitude 精度
     * @param home      用户自定义首页
     */
    @Override
    public void updateUserInfo(Long userId, String nickName, String signature, String address, Double latitude, Double longitude, String home) {
        // 先根据id获取user对象
        if (userId == null) {
            throw new BusinessException(PARAMS_ERROR, "userId不能为null");
        }
        User user = getUserInfoById(userId);

        if (StrUtil.isNotEmpty(nickName)) {
            user.setNickName(nickName);
        }


        if (StrUtil.isNotEmpty(signature)) {
            user.setSignature(signature);
        }

        if (StrUtil.isNotEmpty(home)) {
            user.setHome(home);
        }

        if (StrUtil.isNotEmpty(address) && latitude != null && longitude != null) {
            user.setAddress(address);
            user.setLatitude(latitude);
            user.setLongitude(longitude);
        }

        user.setUpdateTime(LocalDateTime.now());

        int i = userMapper.updateById(user);
        if (i == 0) {
            throw new BusinessException(SYSTEM_ERROR, "更新失败");
        }

        // 更新redis缓存
        stringRedisTemplate.opsForValue().set(USER_INFO + userId, JSONUtil.toJsonStr(user), USER_INFO_TTL, TimeUnit.MINUTES);
    }


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        String token = request.getHeader("X-Token");
        if (token == null) {
            throw new BusinessException(PARAMS_ERROR, "用户未登录");
        }

        Long id = Long.valueOf((String) StpUtil.getLoginIdByToken(token));

        return (User) StpUtil.getSessionByLoginId(id).get(SESSION_USER);
    }


    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && ADMIN_ROLE.equals(user.getRole());
    }


    /**
     * 实现验证 验证码和手机号是否匹配
     *
     * @param phone phone
     * @param code  验证码
     * @param key   redis存储对应验证码的前缀
     * @return 是否匹配
     */
    @Override
    public Boolean verifyPhone(String phone, String code, String key) {

        String cacheCode = stringRedisTemplate.opsForValue().get(key + phone);

        return StrUtil.equals(code, cacheCode);
    }

    /**
     * 更新用户头像
     *
     * @param icon icon
     * @return success
     */
    @Override
    public String updateIcon(Long userId, MultipartFile icon) {
        // 先根据id获取user对象
        if (userId == null) {
            throw new BusinessException(PARAMS_ERROR, "userId不能为null");
        }
        User user = getUserInfoById(userId);

        if (StrUtil.isNotEmpty(user.getIcon())) {
            // 获取旧文件名
            String[] oldFilePath = user.getIcon().split("/");
            String oldFileName = oldFilePath[oldFilePath.length - 1];
            // 删除旧文件
            FileUtils.delete(FILE_ICON_SUB_PATH, oldFileName);

        }

        String path;
        try {
            path = FileUtils.upload(FILE_ICON_SUB_PATH, icon);
            user.setIcon(path);
            int i = userMapper.updateById(user);
            if (i == 0) {
                log.error("更新用户头像失败");
                throw new BusinessException(SYSTEM_ERROR, "更新头像失败");
            }
            // 更新redis缓存
            stringRedisTemplate.opsForValue().set(USER_INFO + userId, JSONUtil.toJsonStr(user), USER_INFO_TTL, TimeUnit.MINUTES);
        } catch (IOException e) {
            log.error("上传头像失败=====》{}", e.getLocalizedMessage());
            throw new BusinessException(SYSTEM_ERROR, e.getLocalizedMessage());
        }

        return path;
    }

    /**
     * 根据id查询昵称
     *
     * @return success
     */
    @Override
    public BaseResponse<String> getNameById(Long publisherId) {

        return ResultUtils.success(getById(publisherId).getNickName());
    }


}




