package com.fzu.crowdsense.model.request.task;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fzu.crowdsense.common.PageRequest;
import lombok.Data;

import java.util.Date;

@Data
public class TaskQueryRequest extends PageRequest {

    private Integer sortNumber = 0;

    private Long id;

    private Long rootId;

    private Long publisherId;

    private String type;

    private String title;

    private String details;

    private String imagesPath;

    private String submitLimit;

    private Long maxPassed;

    private Long currentPassed;

    private Long completedSmallTask;

    private Long NumberOfSmallTask;

    private Double longitude;

    private Double latitude;

    private Integer size;

    private Double integration;

    private Integer onlineStatus;

    private Integer submitStatus;

    private Integer checkStatus;

    private String invalidationReason;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
