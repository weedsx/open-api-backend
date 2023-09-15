package com.weeds.dubboapi.service;

import com.weeds.openapi.common.model.entity.InterfaceInfo;

/**
 * @author weeds
 */
public interface InnerInterfaceInfoService {
    /**
     * 根据路径和方法类型获取调用的接口信息
     *
     * @param url
     * @param method
     * @return
     */
    InterfaceInfo getInvokeInterfaceInfo(String url, String method);
}
