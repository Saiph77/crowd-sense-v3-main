package com.fzu.crowdsense.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息状态枚举
 *
 * @author yupi
 */
public enum TaskStatusEnum {

    OFFLINE("关闭", 0),
    ONLINE("上线", 1),
    UNCHECKED("待审核",0),
    CHECKED("审核通过",1),
    REJECT("审核不通过",2);

    private final String text;

    private final int value;

    TaskStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
