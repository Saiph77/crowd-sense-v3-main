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
@TableName(value = "task_submit")
@Accessors(chain = true)
public class TaskSubmit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer taskPublishId;

    private Integer afferentTaskPublishId;

    private Integer submitterId;

    private String textDescription;

    private Float numericalValue;

    private String filesPath;

    /**
     * 0表示"待审核"，1表示"已通过"，2表示"不合格"
     */
    private Integer status;

    //  这个表示是审核的时间
    private LocalDateTime checkTime;
    //  这个是审核结果的理由
    private String reason;
    //  这个是预留的字段，主要是数据的时间
    private LocalDateTime completeTime;

    private Double longitude;

    private Double latitude;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String nickName;

    @TableField(exist = false)
    private String title;

    @TableField(exist = false)
    private String submitNickName;

}
