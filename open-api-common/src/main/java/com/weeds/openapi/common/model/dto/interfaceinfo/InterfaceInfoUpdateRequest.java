package com.weeds.openapi.common.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author weeds
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {
    private static final long serialVersionUID = -6873801404298864047L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * url地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

}
