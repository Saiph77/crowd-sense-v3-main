package com.fzu.crowdsense.model.request.collection;

import lombok.Data;

@Data
public class AddCollectionRequest {
    private static final long serialVersionUID = 3191241716373120793L;

    private Long userId;

    private Long taskId;
}
