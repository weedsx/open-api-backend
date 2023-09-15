package com.weeds.openapi.constant;

/**
 * redis key
 * @author weeds
 */
public interface RedisKeyConstant {
    String PROJECT_NAME = "open-api-backend.";
    String INCREASE_INVOKE_COUNT_BY_ONE = PROJECT_NAME + "UserInterfaceCountService.increaseInvokeCountByOne:lock";
}
