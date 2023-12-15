package com.fzu.crowdsense.controller;

import com.fzu.crowdsense.annotation.AuthCheck;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.constant.TypeConstant;
import com.fzu.crowdsense.model.entity.Type;
import com.fzu.crowdsense.service.TypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Type)表控制层
 *
 * @author makejava
 * @since 2023-05-12 09:13:20
 */
@RestController
@RequestMapping("type")
public class TypeController{

    @Resource
    private TypeService typeService;

    /**
     * 查询现有type
     * @return
     */
    @GetMapping("/select")
    public BaseResponse<List<String>> selectAllType() {
        return typeService.getAllType();
    }

    /**
     * 新增type
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> addNewType(String typeName) {
        return typeService.addNewType(typeName);
    }

    /**
     * 更新type状态为不可用
     * @return
     */
    @PostMapping("/stop")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> stopType(Integer id) {
        Type type = new Type();
        type.setStatu(TypeConstant.STOP_USE);
        type.setId(id);
        boolean result = typeService.updateById(type);
        return ResultUtils.success(result);
    }

    /**
     * 更新type状态为可用
     * @return
     */
    @PostMapping("/reuse")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> reuseType(Integer id) {
        Type type = new Type();
        type.setStatu(TypeConstant.NORMAL_USE);
        type.setId(id);
        boolean result = typeService.updateById(type);
        return ResultUtils.success(result);
    }


}

