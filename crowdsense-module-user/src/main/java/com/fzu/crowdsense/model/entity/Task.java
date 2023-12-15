package com.fzu.crowdsense.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName task
 */
@TableName(value ="task")
@Data

public class Task implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Id
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    /**
     * 
     */
    @TableField(value = "rootId")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long rootId;

    /**
     * 
     */
    @TableField(value = "publisherId")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long publisherId;

    /**
     * 
     */
    @TableField(value = "type")
    private String type;

    /**
     * 
     */
    @TableField(value = "title")
    private String title;

    /**
     * 
     */
    @TableField(value = "details")
    private String details;

    /**
     * 
     */
    @TableField(value = "imagesPath")
    private String imagesPath;

    /**
     * 
     */
    @TableField(value = "submitLimit")
    private String submitLimit;

    /**
     * 
     */
    @TableField(value = "maxPassed")
    private Long maxPassed;

    /**
     * 
     */
    @TableField(value = "currentPassed")
    private Long currentPassed;

    /**
     *大任务中已完成小任务的数量，小任务的该项值为-1
     */
    @TableField(value = "completedSmallTask")
    private Long completedSmallTask;

    /**
     *包含小任务总数量，小任务的该项值为-1
     */
    @TableField(value = "NumberOfSmallTask")
    private Long NumberOfSmallTask;

    /**
     * 
     */
    @TableField(value = "longitude")
    private Double longitude;

    /**
     * 
     */
    @TableField(value = "latitude")
    private Double latitude;

    /**
     * 网格大小，单位为?
     */
    @TableField(value = "size")
    private Integer size;

    /**
     * 
     */
    @TableField(value = "integration")
    private Double integration;

    /**
     * 0表示“下线”，1表示“上线”
     */
    @TableField(value = "onlineStatus")
    private Integer onlineStatus;

    /**
     * 0表示“未完成”，1表示“已完成”，2表示“失效”
     */
    @TableField(value = "submitStatus")
    private Integer submitStatus;

    /**
     * 0表示“待审核”，1表示“审核通过”，2表示“未通过”
     */
    @TableField(value = "checkStatus")
    private Integer checkStatus;

    /**
     * 审核不通过的理由
     */
    @TableField(value = "invalidationReason")
    private String invalidationReason;

    /**
     * 
     */
    @TableField(value = "startTime")
    private Date startTime;

    /**
     * 
     */
    @TableField(value = "endTime")
    private Date endTime;

    /**
     * 
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}