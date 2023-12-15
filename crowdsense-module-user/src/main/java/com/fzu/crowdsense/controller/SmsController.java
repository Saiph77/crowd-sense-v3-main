package com.fzu.crowdsense.controller;

import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.model.request.sms.SendSMSRequest;
import com.fzu.crowdsense.service.SmsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.fzu.crowdsense.constant.RedisConstants.*;

/**
 * <p>
 * 短信服务
 * <p>
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-04-24
 **/

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Resource
    private SmsService smsService;


    @PostMapping("/send")
    public BaseResponse<String> sendSMS(@Valid @RequestBody SendSMSRequest smsRequest) {
        String type = smsRequest.getType();
        String phone = smsRequest.getPhone();

        switch (type) {
            case "register":
                smsService.sendSms(phone, REGISTER_CODE_KEY);
                break;
            case "login":
                smsService.sendSms(phone, LOGIN_CODE_KEY);
                break;
            case "update":
                smsService.sendSms(phone, UPDATE_CODE_KEY);
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        return ResultUtils.success(null);
    }
}
