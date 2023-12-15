package com.fzu.crowdsense.constant;

import lombok.Data;

@Data
public class QueryPageParam {
    //默认
    private static int PAGE_SIZE=20;
    private static int PAGE_NUM=1;

    private int pageSize=PAGE_SIZE;
    private int pageNum=PAGE_NUM;
}
