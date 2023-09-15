package com.weeds.openapi.common.model.dto.userinterface;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 * @author weeds
 */
@Data
public class UserInterfaceUpdateRequest implements Serializable {
    private static final long serialVersionUID = -8280727059010969917L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 调用次数
     */
    private Integer invokeCount;

    /**
     * 剩余调用次数
     */
    private Integer leftCount;

    /**
     * 状态（0-正常，1-禁用）
     */
    private Integer status;

}
