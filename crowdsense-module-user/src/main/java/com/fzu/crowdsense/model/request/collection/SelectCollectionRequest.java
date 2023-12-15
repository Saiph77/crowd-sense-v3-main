package com.fzu.crowdsense.model.request.collection;


import com.fzu.crowdsense.common.PageRequest;
import lombok.Data;


@Data
public class SelectCollectionRequest extends PageRequest {
    private static final long serialVersionUID = 3191241716373120793L;

    private Long userId;

    private Long taskId;
}
