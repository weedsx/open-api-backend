package com.weeds.openapi.common.model.dto.userinterface;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @author weeds
 */
@Data
public class UserInterfaceAddRequest implements Serializable {
    private static final long serialVersionUID = -7038509844056681215L;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 调用次数
     */
    private Integer invokeCount;

    /**
     * 剩余调用次数
     */
    private Integer leftCount;
}
