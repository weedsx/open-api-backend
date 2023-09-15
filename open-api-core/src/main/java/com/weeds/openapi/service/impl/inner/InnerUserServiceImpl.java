package com.weeds.openapi.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weeds.dubboapi.service.InnerUserService;
import com.weeds.openapi.common.common.ErrorCode;
import com.weeds.openapi.common.exception.ThrowUtils;
import com.weeds.openapi.common.model.entity.User;
import com.weeds.openapi.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author weeds
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        ThrowUtils.throwIf(StringUtils.isBlank(accessKey), ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccessKey, accessKey);
        return userMapper.selectOne(wrapper);
    }
}
