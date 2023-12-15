package com.fzu.crowdsense.model.request.task;

import lombok.Data;

import java.util.Date;

@Data
public class TaskAddRequest {

    private Long rootId;

    private Long publisherId;

    private String type;

    private String title;

    private String details;

    private String imagesPath;

    private String submitLimit;

    private Long maxPassed;

    private Double longitude;

    private Double latitude;

    private Integer size;

    private Double integration;

    private Integer onlineStatus = 0;

    private Long currentPassed = 0L;

    private Date startTime;

    private Date endTime;

    private Integer checkStatus = 0;

    private Integer submitStatus = 0;

    private Long completedSmallTask = 0L;

    private Long NumberOfSmallTask = 0L;

}
