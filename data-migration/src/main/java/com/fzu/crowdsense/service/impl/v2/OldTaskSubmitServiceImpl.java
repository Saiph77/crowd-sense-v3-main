package com.fzu.crowdsense.service.impl.v2;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.entity.v2.TaskSubmit;
import com.fzu.crowdsense.mapper.v2.TaskSubmitMapper;
import com.fzu.crowdsense.service.v2.OldTaskSubmitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zaki
 * @since 2023-07-18
 **/
@DS("old")
@Service
public class OldTaskSubmitServiceImpl extends ServiceImpl<TaskSubmitMapper, TaskSubmit> implements OldTaskSubmitService {

    @Resource
    private TaskSubmitMapper taskSubmitMapper;

    @Override
    public List<TaskSubmit> getAllTaskSubmits() {
        return taskSubmitMapper.selectList(null);

    }
}
