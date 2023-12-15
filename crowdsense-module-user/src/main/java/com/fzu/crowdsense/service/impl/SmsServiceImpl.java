package com.fzu.crowdsense.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.service.SmsService;
import com.fzu.crowdsense.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.fzu.crowdsense.constant.RedisConstants.PHONE_CODE_TTL;

/**
 * <p>
 * 实现SmsService接口
 * <p>
 *
 * @author Zaki
 * @version 2.0
 * @since 2023-04-24
 **/
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AsyncClient client;

    /**
     * 实现判断是否发送过短信
     *
     * @param phone 手机号
     * @param key   redis前缀
     * @return true or false
     */
    @Override
    public Boolean hasSendSms(String phone, String key) {
        String s = stringRedisTemplate.opsForValue().get(key + phone);
        return StrUtil.isNotEmpty(s);
    }


    /**
     * 实现发送短信功能
     *
     * @param phone 手机号
     * @param key   redis前缀
     */
    @Override
    public void sendSms(String phone, String key) {
        // 先检查是否发送过短信
        if (hasSendSms(phone, key)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "短信已发送，请勿频繁操作");
        }

        String code = RandomUtil.randomNumbers(6);

        // 发送短信
        SendSmsRequest smsRequest = SmsUtil.getSMSRequest(phone, code);

        CompletableFuture<SendSmsResponse> response = client.sendSms(smsRequest);

        // 获取响应请求
        SendSmsResponse resp;
        try {
            resp = response.get();
        } catch (Exception e) {
            log.error("SMS短信服务请求失败, 手机号: {} =====》{}", phone, e.getLocalizedMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发送短信失败");
        }

        // 检查是否发送成功
        JSONObject object = new JSONObject(resp);

        JSONObject body = object.getJSONObject("body");

        if (!Objects.equals(body.get("code"), "OK")) {
            log.error("SMS短信服务请求失败, 手机号: {} ======>{}", phone, body.get("message"));
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发送短信失败");
        }

        stringRedisTemplate.opsForValue().set(key + phone, code, PHONE_CODE_TTL, TimeUnit.MINUTES);
    }
}
