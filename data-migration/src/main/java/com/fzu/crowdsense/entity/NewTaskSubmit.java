package com.fzu.crowdsense.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zaki
 * @since 2023-07-18
 **/

@TableName(value = "task_submit")
@Accessors(chain = true)
@Data
public class NewTaskSubmit implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    /**
     *
     */
    @TableField(value = "taskId")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long taskId;

    /**
     *
     */
    @TableField(value = "submitterId")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long submitterId;

    /**
     * 用于获取用户喜好 这个字段在提交时前端给定 不对用户开放
     */
    @TableField(value = "type")
    private String type;

    /**
     *
     */
    @TableField(value = "description")
    private String description;

    /**
     *
     */
    @TableField(value = "numericalValue")
    private Double numericalValue;

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
     *
     */
    @TableField(value = "filesPath")
    private String filesPath;

    /**
     * 0表示"待审核"，1表示"已通过"，2表示"不合格"
     */
    @TableField(value = "status")
    private Integer status;

    /**
     *
     */
    @TableField(value = "checkTime")
    private Date checkTime;

    /**
     *
     */
    @TableField(value = "reason")
    private String reason;

    /**
     * 创建时间
     */
    @TableField(value = "completeTime")
    private Date completeTime;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新保存时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     *
     */
    @TableField(value = "rootTaskId")
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long rootTaskId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
