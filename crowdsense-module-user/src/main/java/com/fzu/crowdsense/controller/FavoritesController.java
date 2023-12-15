package com.fzu.crowdsense.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.PageRequest;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.constant.QueryPageParam;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.model.entity.Favorites;
import com.fzu.crowdsense.model.request.collection.AddCollectionRequest;
import com.fzu.crowdsense.model.request.collection.SelectCollectionRequest;
import com.fzu.crowdsense.model.vo.FavoritesVO;
import com.fzu.crowdsense.model.vo.TaskVO;
import com.fzu.crowdsense.service.FavoritesService;
import com.fzu.crowdsense.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.fzu.crowdsense.common.ErrorCode.NULL_ERROR;
import static com.fzu.crowdsense.common.ErrorCode.PARAMS_ERROR;
import static com.fzu.crowdsense.constant.FavoritesConstant.FINISHED;
import static com.fzu.crowdsense.constant.FavoritesConstant.UN_FINISHED;


/**
 * 前端控制
 *
 * @author fzu.crowdsense
 * @since 2023-03-11
 */
@Api
@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    @Resource
    private FavoritesService favoritesService;

    @Resource
    private Snowflake snowflake;

    @Resource
    private TaskService taskService;

    // region 增删改查

    //增
    @PostMapping("/add")
    public BaseResponse<ErrorCode> addCollection(@RequestBody AddCollectionRequest addCollectionRequest) {
        if (addCollectionRequest == null) {
            return ResultUtils.error(NULL_ERROR);
        }

        Long userId = Long.valueOf((String) StpUtil.getLoginId());
        Long taskId = addCollectionRequest.getTaskId();
        Long id = snowflake.nextId();

        BaseResponse result = favoritesService.addCollection(id, userId, taskId);

        return result;
    }


    //删
    @ApiOperation("通过Id进行删除")
    @DeleteMapping("/deleteById")
    @ApiImplicitParam(name = "id", dataTypeClass = Integer.class, required = true)
    public BaseResponse<Boolean> deleteCollectionById(Long taskId, HttpServletRequest request) {

        if (taskId == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        Long userId = Long.valueOf((String) StpUtil.getLoginId());

        LambdaQueryWrapper<Favorites> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Favorites::getTaskId,taskId);
        lambdaQueryWrapper.eq(Favorites::getUserId,userId);

        return ResultUtils.success(favoritesService.remove(lambdaQueryWrapper));

    }

    //改
    @ApiOperation("更新")
    @PostMapping("/update")
    @ApiImplicitParam(name = "favorites",type = "body", dataTypeClass = Favorites.class, required = true)
    public BaseResponse<Boolean> updateCollectionById(@RequestBody Favorites favorites) {
        if (favorites == null){
            return ResultUtils.error(NULL_ERROR);
        }
        return ResultUtils.success(favoritesService.updateById(favorites));
    }

    //查(根据userid和taskid）
    @ApiOperation("查询")
    @PostMapping("/select")
    @ApiImplicitParam(name = "SelectCollectionRequest",type = "body", dataTypeClass = SelectCollectionRequest.class, required = true)
    public BaseResponse<List<Favorites>> selectCollectionById(@RequestBody SelectCollectionRequest selectCollectionRequest) {
        //判断是否为空
        if (selectCollectionRequest == null) {
            return ResultUtils.error(NULL_ERROR);
        }

        Long userId = selectCollectionRequest.getUserId();
        Long taskId = selectCollectionRequest.getTaskId();
        Long current = selectCollectionRequest.getCurrent();
        Long pageSize = selectCollectionRequest.getPageSize();
        QueryPageParam queryPageParam = new QueryPageParam();


        LambdaQueryWrapper<Favorites> lambdaQueryWrapper = new LambdaQueryWrapper();

        if (userId != null) {
            lambdaQueryWrapper.eq(Favorites::getUserId, userId);
        }

        if (taskId != null) {
            lambdaQueryWrapper.eq(Favorites::getTaskId, taskId);
        }

        queryPageParam.setPageNum(current.intValue());

        queryPageParam.setPageSize(pageSize.intValue());


        Page<Favorites> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());
        page.setOptimizeCountSql(false);

        IPage<Favorites> result = favoritesService.page(page, lambdaQueryWrapper);

        System.out.println(result.getTotal());
        return ResultUtils.success(result.getRecords());
    }

    // endregion

    /**
     * 通过id进行查询
     *
     * @author Lige
     * @since 2023-04-14
     */
    @PostMapping("getById")
    public BaseResponse<Favorites> getFavoritesById(Long id){
        Favorites favorites = favoritesService.getById(id);
        if (favorites ==null){
            return ResultUtils.error(NULL_ERROR,"id不存在");
        }
        return ResultUtils.success(favorites);
    }

    /**
     * 通过用户id进行删除
     *
     * @author Lige
     * @since 2023-04-14
     */
    @ApiOperation("通过UserId进行删除")
    @DeleteMapping ("/deleteByUserId")
    @ApiImplicitParam(name = "userId", dataTypeClass = Integer.class, required = true)
    public BaseResponse<ErrorCode> deleteCollectionByUserId(Long userId) {
        if (userId == null) {
            return ResultUtils.error(NULL_ERROR);
        }

        if (userId <= 0) {
            return ResultUtils.error(PARAMS_ERROR);
        }
        return favoritesService.deleteByUserId(userId);
    }

    /**
     * 通过任务id进行删除
     *
     * @author Lige
     * @since 2023-04-14
     */
    @ApiOperation("通过TaskId进行删除")
    @DeleteMapping ("/deleteByTaskId")
    @ApiImplicitParam(name = "taskId", dataTypeClass = Integer.class, required = true)
    public BaseResponse<ErrorCode> deleteCollectionByTaskId(Long taskId) {
        if (taskId == null) {
            return ResultUtils.error(NULL_ERROR);
        }

        if (taskId <= 0) {
            return ResultUtils.error(PARAMS_ERROR);
        }
        return favoritesService.deleteByTaskId(taskId);
    }

    //TODO 按照时间顺序显示收藏

    /**
     * 显示某任务收藏总数
     *
     * @author Lige
     * @since 2023-04-14
     */
    @PostMapping("getCountByTaskId")
    public BaseResponse<Long> getCountByTaskId(Long taskId){
        return favoritesService.getCountByTaskId(taskId);
    }

    /**
     * 显示某人收藏了多少任务
     *
     * @author Lige
     * @since 2023-04-14
     */
    @PostMapping("getCountByUserId")
    public BaseResponse<Long> getCountByUserId(Long userId){
        return favoritesService.getCountByUserId(userId);
    }

    /**
     * 判断该任务是否已存在
     *
     * @author Lige
     * @since 2023-04-14
     */
    @GetMapping("/checkFavorites")
    public BaseResponse<Boolean> checkFavorites(Long userId, Long taskId){
        return favoritesService.checkFavorites(userId, taskId);
    }

    /**
     * 通过userId查找收藏
     *
     * @author Lige
     * @since 2023-05-18
     */
    public List<Favorites> selectFavoritesByUserIdAndGroupName(Long userId){
        LambdaQueryWrapper<Favorites> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Favorites::getUserId,userId);
        List<Favorites> favorites = favoritesService.list(lambdaQueryWrapper);
        return favorites;
    }

    /**
     * 更改收藏状态为已完成
     * @author Lige
     * @since 2023-05-31
     * @update 2023-06-01
     **/
    @PostMapping("/finishFavorites")
    public BaseResponse<Boolean> finishFavorites(Long id) {
        Favorites favorite = favoritesService.getById(id);
        favorite.setStatus(FINISHED);
        favorite.setId(id);
        boolean result = favoritesService.updateById(favorite);
        return ResultUtils.success(result);
    }

    /**
     * 更改收藏状态为未完成
     * @author Lige
     * @since 2023-05-31
     * @update 2023-06-01
     **/
    @PostMapping("/unfinishFavorites")
    public BaseResponse<Boolean> unfinishFavorites(Long id) {
        Favorites favorite = favoritesService.getById(id);
        favorite.setStatus(UN_FINISHED);
        favorite.setId(id);
        boolean result = favoritesService.updateById(favorite);
        return ResultUtils.success(result);
    }

    /**
     * 查询当前用户已完成的所有任务
     * @author Lige
     * @since 2023-05-31
     * @update 2023-06-01
     **/
    @PostMapping("/select/FinishedFavorites")
    public BaseResponse<Page<FavoritesVO>> selectFinished(@RequestBody PageRequest pageRequest) {
        //获取当前用户
        Long userId = Long.valueOf((String) StpUtil.getLoginId());
        LambdaQueryWrapper<Favorites> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //仅查询当前用户和已完成的收藏
        lambdaQueryWrapper.eq(Favorites::getUserId, userId);
        lambdaQueryWrapper.eq(Favorites::getStatus, FINISHED);
        //按照时间顺序逆序返回
        lambdaQueryWrapper.orderByDesc(Favorites::getCreateTime);

        //查找page的favorites
        Page<Favorites> favoritesPage =
                favoritesService.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), lambdaQueryWrapper);

        List<Favorites> favoritesList = favoritesPage.getRecords();
        List<FavoritesVO> favoritesVoList = new ArrayList<>();

        //获取具体的task信息
        for (Favorites favorites : favoritesList) {
            Long taskId = favorites.getTaskId();
            TaskVO taskVo = taskService.getTaskVoByTaskId(taskId).getData();
            FavoritesVO favoritesVo = new FavoritesVO();
            //TODO 优化
            BeanUtils.copyProperties(favorites,favoritesVo);
            favoritesVo.setTask(taskVo);
            favoritesVoList.add(favoritesVo);
        }

        //存储到page类型中
        Page<FavoritesVO> favoritesVoPage = new Page<>();
        favoritesVoPage.setRecords(favoritesVoList);
        favoritesVoPage.setCurrent(favoritesPage.getCurrent());
        favoritesVoPage.setSize(favoritesPage.getSize());
        favoritesVoPage.setTotal(favoritesPage.getTotal());
        favoritesVoPage.setPages(favoritesPage.getPages());

        return ResultUtils.success(favoritesVoPage);
    }

    /**
     * 查询当前用户未完成的所有任务
     * @author Lige
     * @since 2023-05-31
     * @update 2023-06-01
     **/
    @PostMapping("/select/UnFinishedFavorites")
    public BaseResponse<Page<FavoritesVO>> selectUnFavorites(@RequestBody PageRequest pageRequest) {
        //获取当前用户
        Long userId = Long.valueOf((String) StpUtil.getLoginId());
        LambdaQueryWrapper<Favorites> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //仅查询当前用户和已完成的收藏
        lambdaQueryWrapper.eq(Favorites::getUserId, userId);
        lambdaQueryWrapper.eq(Favorites::getStatus, UN_FINISHED);
        //按照时间顺序逆序返回
        lambdaQueryWrapper.orderByDesc(Favorites::getCreateTime);

        //查找page的favorites
        Page<Favorites> favoritesPage =
                favoritesService.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()), lambdaQueryWrapper);

        List<Favorites> favoritesList = favoritesPage.getRecords();
        List<FavoritesVO> favoritesVoList = new ArrayList<>();

        //获取具体的task信息
        for (Favorites favorites : favoritesList) {
            Long taskId = favorites.getTaskId();
            TaskVO taskVo = taskService.getTaskVoByTaskId(taskId).getData();
            FavoritesVO favoritesVo = new FavoritesVO();
            //TODO 优化
            BeanUtils.copyProperties(favorites,favoritesVo);
            favoritesVo.setTask(taskVo);
            favoritesVoList.add(favoritesVo);
        }

        //存储到page类型中
        Page<FavoritesVO> favoritesVoPage = new Page<>();
        favoritesVoPage.setRecords(favoritesVoList);
        favoritesVoPage.setCurrent(favoritesPage.getCurrent());
        favoritesVoPage.setSize(favoritesPage.getSize());
        favoritesVoPage.setTotal(favoritesPage.getTotal());
        favoritesVoPage.setPages(favoritesPage.getPages());

        return ResultUtils.success(favoritesVoPage);
    }

}
