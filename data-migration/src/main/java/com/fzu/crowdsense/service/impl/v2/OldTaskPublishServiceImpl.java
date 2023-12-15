package com.fzu.crowdsense.service.impl.v2;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.entity.v2.TaskPublish;
import com.fzu.crowdsense.mapper.v2.TaskPublishMapper;
import com.fzu.crowdsense.service.v2.OldTaskPublishService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zaki
 * @since 2023-07-18
 **/
@DS("old")
@Service
public class OldTaskPublishServiceImpl extends ServiceImpl<TaskPublishMapper, TaskPublish> implements OldTaskPublishService {

    @Resource
    private TaskPublishMapper taskPublishMapper;

    /**
     * 获取所有数据
     *
     * @return 数据集合
     */
    @Override
    public List<TaskPublish> getAll() {
        return taskPublishMapper.selectList(null);
    }
}
