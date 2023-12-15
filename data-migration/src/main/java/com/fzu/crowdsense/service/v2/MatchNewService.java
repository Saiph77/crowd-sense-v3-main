package com.fzu.crowdsense.service.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.entity.v2.MatchNew;

import java.util.Map;


public interface MatchNewService extends IService<MatchNew> {

    Map<Integer,Integer> getAllParentMap();

}