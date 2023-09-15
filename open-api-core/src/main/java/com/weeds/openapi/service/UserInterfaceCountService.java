package com.weeds.openapi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.weeds.openapi.common.model.entity.UserInterfaceCount;

/**
 * @author weeds
 * @description 针对表【user_interface_count(用户接口关系表)】的数据库操作Service
 * @createDate 2023-07-25 15:01:31
 */
public interface UserInterfaceCountService extends IService<UserInterfaceCount> {

    /**
     * 校验
     *
     * @param userInterfaceCount
     * @param verifyNonnullParam
     */
    void verifyUserInterface(UserInterfaceCount userInterfaceCount, boolean verifyNonnullParam);

    /**
     * 接口调用次数加一
     *
     * @param interfaceId
     * @param userId
     * @return
     */
    boolean increaseInvokeCountByOne(long interfaceId, long userId);

    /**
     * 如果是初次调用，为用户分配该接口的调用次数，默认100次
     *
     * @param userId
     * @param interfaceId
     * @return
     */
    boolean initUserInterface(Long userId, Long interfaceId);
}
