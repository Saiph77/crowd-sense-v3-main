package com.fzu.crowdsense.constant;

import java.util.Arrays;
import java.util.List;

public interface TaskConstant {

    /**
     * 下线
     */
    Integer OFFLINE = 0;

    /**
     * 上线
     */
    Integer ONLINE = 1;

    /**
     * 大任务的rootId
     */
    Long BIGTASK_ROOTID = -1L;


    /**
     * 推荐任务数量
     */
    Integer RecommendNUM = 5;

    /**
     * 任务排序条件
     */
    List<String> SORT_NUMBER = Arrays.asList(
            "createTime", "descend",//时间降序
            "endTime", "ascend",//结束时间升序
            "endTime", "descend",//结束时间升序
            "", "",//任务进度
            "NumberOfSmallTask", "descend"//小任务数量降序
    );


    //submitStatus
    /**
     * 任务“未完成”
     */
    Integer UN_COMPLETED = 0;

    /**
     * 任务表示“已完成”
     */
    Integer COMPLETED = 1;
    /**
     * 任务表示“失效”
     */
    Integer TIMEOUT = 2;



}
