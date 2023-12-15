package com.fzu.crowdsense.model.request.task;

import com.fzu.crowdsense.common.PageRequest;
import lombok.Data;

@Data
public class PublisherTaskRequest extends PageRequest {

    //类型，默认3为“全部”，0为“进行中”，1为“已完成”，2为“已失效”
    private Integer group = 3;

}
