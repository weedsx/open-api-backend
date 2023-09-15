package com.weeds.dubboapi.service;

import com.weeds.openapi.common.model.entity.User;

/**
 * @author weeds
 */
public interface InnerUserService {
    /**
     * 数据库中查寻持有该秘钥的用户
     *
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
