package com.fzu.crowdsense.service.impl.v2;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.entity.v2.MatchNew;
import com.fzu.crowdsense.mapper.v2.MatchNewMapper;
import com.fzu.crowdsense.service.v2.MatchNewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@DS("old")
public class MatchNewServiceImpl extends ServiceImpl<MatchNewMapper, MatchNew> implements MatchNewService {

    @Resource
    private MatchNewMapper matchNewMapper;

    @Override
    public Map<Integer, Integer> getAllParentMap() {
        List<MatchNew> matchNews = matchNewMapper.selectList(null);
        Map<Integer, Integer> map = new HashMap<>();
        // 添加任务id和父类任务id的映射
        for (MatchNew m : matchNews) {
            map.put(m.getSmallTaskId(), m.getBigTaskId());
        }

        // 为父类id添加-1映射
        for (MatchNew m : matchNews) {
            map.putIfAbsent(m.getBigTaskId(), -1);
        }

        return map;
    }
}
