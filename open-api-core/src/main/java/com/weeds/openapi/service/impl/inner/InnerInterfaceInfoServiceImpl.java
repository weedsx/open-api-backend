package com.weeds.openapi.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weeds.dubboapi.service.InnerInterfaceInfoService;
import com.weeds.openapi.common.common.ErrorCode;
import com.weeds.openapi.common.exception.ThrowUtils;
import com.weeds.openapi.common.model.entity.InterfaceInfo;
import com.weeds.openapi.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author weeds
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInvokeInterfaceInfo(String url, String method) {
        ThrowUtils.throwIf(StringUtils.isAnyBlank(url, method), ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceInfo::getUrl, url)
                .eq(InterfaceInfo::getMethod, method);
        return interfaceInfoMapper.selectOne(wrapper);
    }
}
