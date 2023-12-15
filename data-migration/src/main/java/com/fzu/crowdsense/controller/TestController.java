package com.fzu.crowdsense.controller;

import com.fzu.crowdsense.service.DataMigrationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author Zaki
 * @version TODO
 * @since 2023-07-18
 **/
@RestController
public class TestController {
    @Resource
    private DataMigrationService service;

    @GetMapping("/transfer/task_publish")
    public String transferTaskPublish() {
        return String.valueOf(service.fromTaskPublishToTask());
    }


    @GetMapping("/transfer/task_submit")
    public String transferTaskSubmit() {
        return String.valueOf(service.taskSubmitFromOldToNew());
    }


}
