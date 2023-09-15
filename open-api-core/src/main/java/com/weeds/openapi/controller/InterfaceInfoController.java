package com.weeds.openapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weeds.client.client.OpenApiClient;
import com.weeds.client.model.Param;
import com.weeds.openapi.annotation.AuthCheck;
import com.weeds.openapi.common.common.BaseResponse;
import com.weeds.openapi.common.common.ErrorCode;
import com.weeds.openapi.common.common.IdRequest;
import com.weeds.openapi.common.common.ResultUtils;
import com.weeds.openapi.common.constant.CommonConstant;
import com.weeds.openapi.common.exception.ThrowUtils;
import com.weeds.openapi.common.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.weeds.openapi.common.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.weeds.openapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.weeds.openapi.common.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.weeds.openapi.common.model.entity.InterfaceInfo;
import com.weeds.openapi.common.model.entity.User;
import com.weeds.openapi.common.model.enums.InterfaceStatusEnum;
import com.weeds.openapi.constant.UserConstant;
import com.weeds.openapi.service.InterfaceInfoService;
import com.weeds.openapi.service.UserInterfaceCountService;
import com.weeds.openapi.service.UserService;
import com.weeds.openapi.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static com.weeds.openapi.common.constant.CommonConstant.METHOD_TYPE_WITH_BODY;

/**
 * @author weeds
 */
@Slf4j
@RestController
@RequestMapping("/interfaceInfo")
public class InterfaceInfoController {
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserInterfaceCountService userInterfaceCountService;

    private final Gson gson = new Gson();

    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(
            @RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(interfaceInfoAddRequest == null, ErrorCode.PARAMS_ERROR);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 对参数进行校验
        interfaceInfoService.verifyInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setCreatorId(loginUser.getId());
        boolean save = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(interfaceInfo.getId());
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(
            @RequestBody IdRequest deleteRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员才可删除
        ThrowUtils.throwIf(!Objects.equals(oldInterfaceInfo.getCreatorId(), loginUser.getId())
                        && !userService.isAdmin(loginUser),
                ErrorCode.NO_AUTH_ERROR);
        return ResultUtils.success(interfaceInfoService.removeById(id));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(
            @RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 校验参数
        interfaceInfoService.verifyInterfaceInfo(interfaceInfo, false);
        User loginUser = userService.getLoginUser(request);
        Long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员才可修改
        ThrowUtils.throwIf(!Objects.equals(loginUser.getId(), oldInterfaceInfo.getCreatorId())
                        && !userService.isAdmin(loginUser),
                ErrorCode.NO_AUTH_ERROR);
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }

    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(interfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(interfaceInfo);
    }

    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(
            InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        ThrowUtils.throwIf(interfaceInfoQueryRequest == null, ErrorCode.PARAMS_ERROR);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfo);
        QueryWrapper<InterfaceInfo> wrapper = new QueryWrapper<>(interfaceInfo);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(wrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    @GetMapping("/page")
    public BaseResponse<Page<InterfaceInfo>> pageInterfaceInfo(
            InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        ThrowUtils.throwIf(interfaceInfoQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = interfaceInfoQueryRequest.getCurrent();
        long pageSize = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQueryRequest.getDescription();
        // 将description设置为空，不加入and拼接，我们手动模糊查询
        interfaceInfoQueryRequest.setDescription(null);
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfo);

        QueryWrapper<InterfaceInfo> wrapper = new QueryWrapper<>(interfaceInfo);
        wrapper.like(StringUtils.isNotBlank(description), "description", description)
                // todo: 枚举排序字段
                .orderBy(SqlUtils.validSortField(sortField),
                        CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, pageSize), wrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        ThrowUtils.throwIf(idRequest == null || idRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Long id = idRequest.getId();
        // 判断接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // todo: 判该接口是否可以调用

        // 仅管理员才可修改，注解已校验
        InterfaceInfo interfaceInfo = new InterfaceInfo() {{
            this.setId(id);
            this.setStatus(InterfaceStatusEnum.ONLINE.getValue());
        }};
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }

    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        ThrowUtils.throwIf(idRequest == null || idRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Long id = idRequest.getId();
        // 判断接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员才可修改，注解已校验
        InterfaceInfo interfaceInfo = new InterfaceInfo() {{
            setId(id);
            setStatus(InterfaceStatusEnum.OFFLINE.getValue());
        }};
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }

    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(
            @RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(interfaceInfoInvokeRequest == null
                        || interfaceInfoInvokeRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        Long interfaceId = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 接口是否存在、是否可用
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        ThrowUtils.throwIf(interfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(InterfaceStatusEnum.OFFLINE.getValue() == interfaceInfo.getStatus(),
                ErrorCode.SYSTEM_ERROR, "接口已关闭");

        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        // 如果是初次调用，就为用户分配该接口的调用次数，默认100次
        boolean initUserInterfaceCount = userInterfaceCountService.initUserInterface(loginUser.getId(), interfaceId);
        ThrowUtils.throwIf(!initUserInterfaceCount, ErrorCode.OPERATION_ERROR);
        // 创建 SDK 客户端
        OpenApiClient openApiClient = new OpenApiClient(accessKey, secretKey);
        // 包含请求体的请求
        if (METHOD_TYPE_WITH_BODY.contains(interfaceInfo.getMethod())) {
            String result = openApiClient.post(userRequestParams, interfaceInfo.getUrl());
            return ResultUtils.success(result);
        } else {
            List<Param> paramList = gson.fromJson(userRequestParams, new TypeToken<List<Param>>() {
            }.getType());
            String result = openApiClient.getByList(paramList, interfaceInfo.getUrl());
            return ResultUtils.success(result);
        }
    }
}
