package com.weeds.openapi.common.model.dto.userinterface;

import com.weeds.openapi.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author weeds
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceQueryRequest extends PageRequest
        implements Serializable {
    private static final long serialVersionUID = 9093483380331880312L;
    /**
     * 主键
     */
    private Long id;

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

    /**
     * 状态（0-正常，1-禁用）
     */
    private Integer status;
}
