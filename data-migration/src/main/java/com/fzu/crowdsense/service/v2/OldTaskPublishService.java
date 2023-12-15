package com.fzu.crowdsense.service.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.entity.v2.TaskPublish;

import java.util.List;

/**
 * 旧数据库的Service类
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-07-18
 **/
public interface OldTaskPublishService extends IService<TaskPublish> {

    /**
     * 获取表中所有数据
     *
     * @return 数据集合
     */
    List<TaskPublish> getAll();
}
