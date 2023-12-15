package com.fzu.crowdsense.entity.v2;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author 蔡启铨
 * @since 2021-06-18
 */
@Data
@Accessors(chain = true)
@TableName(value = "task_publish")
public class TaskPublish implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer publisherId;

    private Integer type;

    private String title;

    /**
     * 0表示“未完成”，1表示“已完成”，2表示“失效”
     */
    private Integer status;

    private String details;

    private String imagesPath;

    private String submitLimit;

    private Double longitude;

    private Double latitude;

    private Integer maxPassed;

    private Integer currentPassed;

    private Float integration;

    private Integer size;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String nickName;

    @TableField(exist = false)
    private String profilePath;


}
