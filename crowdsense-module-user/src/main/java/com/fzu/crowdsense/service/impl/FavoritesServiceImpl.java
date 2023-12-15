package com.fzu.crowdsense.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.mapper.FavoritesMapper;
import com.fzu.crowdsense.model.entity.Favorites;
import com.fzu.crowdsense.service.FavoritesService;
import com.fzu.crowdsense.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.fzu.crowdsense.common.ErrorCode.*;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wms
 * @since 2023-03-11
 */
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {

    @Resource
    private TaskService taskService;

    @Resource
    private FavoritesService favoritesService;

    @Override
    public BaseResponse<ErrorCode> addCollection(Long id, Long user_id, Long task_id) {
        if (user_id == null || task_id == null){
            return ResultUtils.error(NULL_ERROR,"数据为空");
        }

        if (taskService.countByTaskId(task_id) == 0L){
            return ResultUtils.error(PARAMS_ERROR,"该任务不存在");
        }
        //判断该收藏关系是否已经存在
        if(favoritesService.checkFavorites(user_id,task_id).getData()){
            return ResultUtils.error(PARAMS_ERROR,"该收藏已存在");
        }

        //实现add功能
        Favorites favorites = new Favorites();

        LocalDateTime localDateTime = LocalDateTime.now();
        favorites.setId(id);
        favorites.setUserId(user_id);
        favorites.setTaskId(task_id);
        favorites.setCreateTime(localDateTime);
        favorites.setUpdateTime(localDateTime);

        boolean saveResult = this.save(favorites);
        if (saveResult){
            return ResultUtils.success(SUCCESS);
        }else{
            return ResultUtils.error(SYSTEM_ERROR);
        }
    }

    @Override
    public BaseResponse<ErrorCode> deleteByUserId(Long userId) {
        //TODO 判断userId是否存在

        QueryWrapper<Favorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Favorites::getUserId,userId);
        Boolean deleteResult = remove(queryWrapper);

        if (deleteResult){
            return ResultUtils.success(SUCCESS);
        }else{
            return ResultUtils.error(SYSTEM_ERROR);
        }
    }

    @Override
    public BaseResponse<ErrorCode> deleteByTaskId(Long taskId) {
        //判断taskId是否存在

        QueryWrapper<Favorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Favorites::getTaskId,taskId);
        Boolean deleteResult = remove(queryWrapper);

        if (deleteResult){
            return ResultUtils.success(SUCCESS);
        }else{
            return ResultUtils.error(SYSTEM_ERROR);
        }
    }

    @Override
    public BaseResponse<Boolean> checkFavorites(Long userId, Long taskId) {
        LambdaQueryWrapper<Favorites> favoritesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoritesLambdaQueryWrapper.eq(Favorites::getUserId, userId);
        favoritesLambdaQueryWrapper.eq(Favorites::getTaskId, taskId);

        Long count = (long)favoritesService.count(favoritesLambdaQueryWrapper);
        return ResultUtils.success(count != 0);
    }

    @Override
    public BaseResponse<Long> getCountByTaskId(Long taskId) {
        //TODO 判断taskId是否存在且合法

        LambdaQueryWrapper<Favorites> favoritesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoritesLambdaQueryWrapper.eq(Favorites::getTaskId, taskId);
        Long count = (long)favoritesService.count(favoritesLambdaQueryWrapper);
        return ResultUtils.success(count);
    }

    @Override
    public BaseResponse<Long> getCountByUserId(Long userId) {
        //TODO 判断userId是否存在且合法

        LambdaQueryWrapper<Favorites> favoritesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoritesLambdaQueryWrapper.eq(Favorites::getUserId, userId);
        Long count = (long)favoritesService.count(favoritesLambdaQueryWrapper);
        return ResultUtils.success(count);
    }


}
