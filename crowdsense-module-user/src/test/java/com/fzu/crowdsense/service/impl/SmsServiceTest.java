package com.fzu.crowdsense.service.impl;

import com.fzu.crowdsense.UserModuleApplication;
import com.fzu.crowdsense.service.SmsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static com.fzu.crowdsense.constant.RedisConstants.REGISTER_CODE_KEY;

/**
 * <p>
 *  TODO
 * <p>
 *
 * @author Zaki
 * @version TODO
 * @since 2023-04-24
 **/

@SpringBootTest(classes = UserModuleApplication.class)
public class SmsServiceTest {

    @Resource
    private SmsService service;

    @Test
    void testSendSms(){
        service.sendSms("",REGISTER_CODE_KEY);
    }
}
