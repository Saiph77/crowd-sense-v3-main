package com.fzu.crowdsense.utils;

import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;

/**
 * <p>
 * 短信发送封装类
 * <p>
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-04-24
 **/
public class SmsUtil {

    public static SendSmsRequest getSMSRequest(String phone, String code) {
        return SendSmsRequest.builder()
                .signName("urbansensing")
                .templateCode("SMS_277565020")
                .phoneNumbers(phone)
                .templateParam("{\"code\":\"" + code + "\"}")
                .build();
    }
}
