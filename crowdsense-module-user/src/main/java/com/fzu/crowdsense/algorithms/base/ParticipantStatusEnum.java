package com.fzu.crowdsense.algorithms.base;

/**
 *  参与者状态枚举
 * @author Xiong
 * @date 2020/5/22 15:46
 */
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipantStatusEnum {
    AVAILABLE("AVAILABLE", "空闲中"),
    BUSY("BUSY", "忙碌中"),
    DISABLED("DISABLE", "不可用");

    private String value;

    private String message;
}
