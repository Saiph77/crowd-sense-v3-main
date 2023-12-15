package com.fzu.crowdsense.model.request.task;

import lombok.Data;

import java.util.Date;

@Data
public class TaskUpdateRequest {

    private Long id;

    private String type;

    private String title;

    private String details;

    private String imagesPath;

    private Long maxPassed;

    private Double longitude;

    private Double latitude;

    private Integer size;

    private Double integration;

    private Integer onlineStatus;

    private Date startTime;

    private Date endTime;

    private String submitLimit;
}
