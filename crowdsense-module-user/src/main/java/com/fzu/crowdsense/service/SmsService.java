package com.fzu.crowdsense.service;

/**
 * <p>
 * 短信发送接口
 * <p>
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-04-24
 **/
public interface SmsService {

    /**
     * 查看是否发送过短信
     *
     * @param phone 手机号
     * @param key   redis前缀
     * @return true or false
     */
    Boolean hasSendSms(String phone, String key);


    /**
     * 发送短信
     *
     * @param phone 手机号
     * @param key   redis前缀
     */
    void sendSms(String phone, String key);
}
