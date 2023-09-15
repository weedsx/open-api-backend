package com.weeds.openapi.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 *
 * @author weeds
 * @TableName interface_info
 */
@TableName(value = "interface_info")
@Data
public class InterfaceInfo implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = -5714654050543583412L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * url地址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 请求参数
     */
    @TableField(value = "request_params")
    private String requestParams;

    /**
     * 请求头
     */
    @TableField(value = "request_header")
    private String requestHeader;

    /**
     * 响应头
     */
    @TableField(value = "response_header")
    private String responseHeader;

    /**
     * 请求类型
     */
    @TableField(value = "method")
    private String method;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建人id
     */
    @TableField(value = "creator_id")
    private Long creatorId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableField(value = "is_deleted")
    @TableLogic
    private Integer isDeleted;
}