package com.fzu.crowdsense.service;

/**
 * 数据迁移Service类
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-07-21
 **/
public interface DataMigrationService {

    /**
     * 将crowdsense_v2--task_publish表中的数据迁移到crowdsense--task表中
     *
     * @return 迁移的数据数
     */
    Integer fromTaskPublishToTask();


    /**
     * 将crowdsense_v2--task_submit表中的数据迁移到crowdsense--task_submit表中
     *
     * @return 迁移的数据数
     */
    Integer taskSubmitFromOldToNew();


}
