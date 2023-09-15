package com.weeds.openapi.common.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 *
 * @author weeds
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {
    private static final long serialVersionUID = 7937179841001188871L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;
}
