package com.weeds.openapi.service.impl.inner;

import com.weeds.dubboapi.service.InnerUserInterfaceCountService;
import com.weeds.openapi.service.UserInterfaceCountService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author weeds
 */
@DubboService
public class InnerUserInterfaceCountServiceImpl implements InnerUserInterfaceCountService {
    @Resource
    private UserInterfaceCountService userInterfaceCountService;

    @Override
    public void increaseInvokeCount(long interfaceId, long userId) {
        userInterfaceCountService.increaseInvokeCountByOne(interfaceId, userId);
    }

    @Override
    public void initUserInterfaceCount(long interfaceId, long userId) {
        userInterfaceCountService.initUserInterface(userId, interfaceId);
    }
}
