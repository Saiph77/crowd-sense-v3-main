package com.fzu.crowdsense.model.vo;

import lombok.Data;

@Data
public class FavoritesVO {
    private Long id;

    private Long userId;

    private Long taskId;

    private TaskVO task;

    private String status;

}
