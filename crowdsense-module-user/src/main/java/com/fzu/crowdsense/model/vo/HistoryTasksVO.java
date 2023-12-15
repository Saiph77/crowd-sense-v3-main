package com.fzu.crowdsense.model.vo;

import lombok.Data;

import java.util.Date;


/**
 * @author Lenovo
 * @version 1.0
 * @description: 历史任务封装类
 * @date 2023/5/15 11:11
 *
 */
@Data
public class HistoryTasksVO {


    private Long userId;

    private Long taskId;

//    private Task historyTask;

    private Long duration;

    private Date submitTime;


    private int type;

    private String title;

    private String details;


    private String submitLimit;

    private Long maxPassed;

    private Long currentPassed;

    private Double longitude;

    private Double latitude;

    private Double integration;

    private Integer onlineStatus;

    private Integer submitStatus;

    private Integer checkStatus;


    private Date startTime;

    private Date endTime;


    public void setDuration(Date endTime, Date startTime) {
        System.out.println(endTime);
        long nd = 1000 * 24 * 60 * 60;
        this.duration = (endTime.getTime() - startTime.getTime()) / nd;
    }

}
