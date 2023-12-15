package com.fzu.crowdsense.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 预留的一张表，目前只设置提交反馈，也就是新增记录功能。
 * </p>
 *
 * @author wms
 * @since 2023-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Feedback对象", description="预留的一张表，目前只设置提交反馈，也就是新增记录功能。")
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    private String advice;

    private String contactDetails;

    private Long feedbackUserId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
