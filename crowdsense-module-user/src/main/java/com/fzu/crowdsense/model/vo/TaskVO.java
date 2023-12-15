package com.fzu.crowdsense.model.vo;

import com.fzu.crowdsense.model.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TaskVO {

    private Long id;

    private String type;

    private String title;

    private String details;

    private String imagesPath;

    private String submitLimit;

    private Long maxPassed;

    private Long currentPassed;

    private Double longitude;

    private Double latitude;

    private Double integration;

    private Integer onlineStatus;

    private Integer submitStatus;

    private Integer checkStatus;

    private String invalidationReason;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private Date updateTime;

    private List<TaskVO> childTask;

    private Long publisherId;

    private User publisherInfo;

//    private String publisherName;

}
