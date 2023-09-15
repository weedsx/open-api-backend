package com.weeds.openapi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.weeds.openapi.common.model.entity.InterfaceInfo;

/**
 * @author weeds
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-07-10 11:57:44
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 校验
     * @param interfaceInfo
     * @param verifyNonnullParam
     */
    void verifyInterfaceInfo(InterfaceInfo interfaceInfo, boolean verifyNonnullParam);
}
