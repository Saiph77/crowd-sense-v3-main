package com.fzu.crowdsense.service.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.entity.v2.TaskSubmit;

import java.util.List;

/**
 * @author Zaki
 * @since 2023-07-18
 **/
public interface OldTaskSubmitService extends IService<TaskSubmit> {

    /**
     * 获取所有数据
     *
     * @return 集合
     */
    List<TaskSubmit> getAllTaskSubmits();
}
