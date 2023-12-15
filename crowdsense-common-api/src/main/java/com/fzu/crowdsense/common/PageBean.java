package com.fzu.crowdsense.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页返回响应
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-07-11
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageBean<T> implements Serializable {

    private static final long serialVersionUID = 2786909754187386310L;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 当前页数
     */
    private Integer currentPage;

    /**
     * 一页的信息数
     */
    private Integer size;

    /**
     * 限制时间戳
     */
    private Long timestamp;

    /**
     * 消息类型
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer type;

    /**
     * 消息总数(包含已读和未读)
     */
    private Integer totalNum;

    /**
     * 未读信息数
     */
    private Integer unreadNum;

    /**
     * 分页查询结果
     */
    private List<T> resultList;
}
