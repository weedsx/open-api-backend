package com.weeds.dubboapi.service;

/**
 * @author weeds
 */
public interface InnerUserInterfaceCountService {
    /**
     * 用户调用次数增加
     *
     * @param interfaceId
     * @param userId
     */
    void increaseInvokeCount(long interfaceId, long userId);

    /**
     * 如果是初次调用，为用户分配该接口的调用次数
     *
     * @param interfaceId
     * @param userId
     */
    void initUserInterfaceCount(long interfaceId, long userId);
}
