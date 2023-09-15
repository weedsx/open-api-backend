package com.weeds.openapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.weeds.openapi.annotation.AuthCheck;
import com.weeds.openapi.common.common.BaseResponse;
import com.weeds.openapi.common.common.ErrorCode;
import com.weeds.openapi.common.common.IdRequest;
import com.weeds.openapi.common.common.ResultUtils;
import com.weeds.openapi.common.exception.ThrowUtils;
import com.weeds.openapi.common.model.dto.userinterface.UserInterfaceAddRequest;
import com.weeds.openapi.common.model.dto.userinterface.UserInterfaceQueryRequest;
import com.weeds.openapi.common.model.dto.userinterface.UserInterfaceUpdateRequest;
import com.weeds.openapi.common.model.entity.User;
import com.weeds.openapi.common.model.entity.UserInterfaceCount;
import com.weeds.openapi.common.constant.CommonConstant;
import com.weeds.openapi.constant.UserConstant;
import com.weeds.openapi.service.UserInterfaceCountService;
import com.weeds.openapi.service.UserService;
import com.weeds.openapi.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author weeds
 */
@Slf4j
@RestController
@RequestMapping("/user-interface")
public class UserInterfaceController {
    @Resource
    private UserInterfaceCountService userInterfaceCountService;

    @Resource
    private UserService userService;

    private final Gson gson = new Gson();

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterface(
            @RequestBody UserInterfaceAddRequest userInterfaceAddRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(userInterfaceAddRequest == null, ErrorCode.PARAMS_ERROR);
        UserInterfaceCount userInterfaceCount = new UserInterfaceCount();
        BeanUtils.copyProperties(userInterfaceAddRequest, userInterfaceCount);
        User loginUser = userService.getLoginUser(request);
        userInterfaceCount.setUserId(loginUser.getId());
        // 对参数进行校验
        userInterfaceCountService.verifyUserInterface(userInterfaceCount, true);
        boolean save = userInterfaceCountService.save(userInterfaceCount);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(userInterfaceCount.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserInterface(
            @RequestBody IdRequest deleteRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceCount oldUserInterface = userInterfaceCountService.getById(id);
        ThrowUtils.throwIf(oldUserInterface == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员才可删除
        ThrowUtils.throwIf(!Objects.equals(oldUserInterface.getUserId(), loginUser.getId())
                        && !userService.isAdmin(loginUser),
                ErrorCode.NO_AUTH_ERROR);
        return ResultUtils.success(userInterfaceCountService.removeById(id));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterface(
            @RequestBody UserInterfaceUpdateRequest userInterfaceUpdateRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(userInterfaceUpdateRequest == null || userInterfaceUpdateRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        UserInterfaceCount userInterfaceCount = new UserInterfaceCount();
        BeanUtils.copyProperties(userInterfaceUpdateRequest, userInterfaceCount);
        // 校验参数
        userInterfaceCountService.verifyUserInterface(userInterfaceCount, false);
        User loginUser = userService.getLoginUser(request);
        Long id = userInterfaceUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceCount oldUserInterface = userInterfaceCountService.getById(id);
        ThrowUtils.throwIf(oldUserInterface == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员才可修改
        ThrowUtils.throwIf(!Objects.equals(loginUser.getId(), oldUserInterface.getUserId())
                        && !userService.isAdmin(loginUser),
                ErrorCode.NO_AUTH_ERROR);
        return ResultUtils.success(userInterfaceCountService.updateById(userInterfaceCount));
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserInterfaceCount> getUserInterfaceById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        UserInterfaceCount oldUserInterface = userInterfaceCountService.getById(id);
        ThrowUtils.throwIf(oldUserInterface == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(oldUserInterface);
    }

    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserInterfaceCount>> listUserInterface(
            UserInterfaceQueryRequest userInterfaceQueryRequest) {
        ThrowUtils.throwIf(userInterfaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        UserInterfaceCount userInterfaceCount = new UserInterfaceCount();
        BeanUtils.copyProperties(userInterfaceQueryRequest, userInterfaceCount);
        QueryWrapper<UserInterfaceCount> wrapper = new QueryWrapper<>(userInterfaceCount);
        List<UserInterfaceCount> interfaceInfoList = userInterfaceCountService.list(wrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    @GetMapping("/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceCount>> pageUserInterface(
            UserInterfaceQueryRequest userInterfaceQueryRequest) {
        ThrowUtils.throwIf(userInterfaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userInterfaceQueryRequest.getCurrent();
        long pageSize = userInterfaceQueryRequest.getPageSize();
        String sortField = userInterfaceQueryRequest.getSortField();
        String sortOrder = userInterfaceQueryRequest.getSortOrder();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        UserInterfaceCount userInterfaceCount = new UserInterfaceCount();
        BeanUtils.copyProperties(userInterfaceQueryRequest, userInterfaceCount);

        QueryWrapper<UserInterfaceCount> wrapper = new QueryWrapper<>(userInterfaceCount);
        // todo: 枚举排序字段
        wrapper.orderBy(SqlUtils.validSortField(sortField),
                CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
        Page<UserInterfaceCount> interfaceInfoPage =
                userInterfaceCountService.page(new Page<>(current, pageSize), wrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

}
