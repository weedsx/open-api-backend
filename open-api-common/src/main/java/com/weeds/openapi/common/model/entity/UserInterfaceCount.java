package com.weeds.openapi.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户接口关系表
 * @author weeds
 * @TableName user_interface_count
 */
@TableName(value ="user_interface_count")
@Data
public class UserInterfaceCount implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 5323418789813086848L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 接口id
     */
    @TableField(value = "interface_id")
    private Long interfaceId;

    /**
     * 调用次数
     */
    @TableField(value = "invoke_count")
    private Integer invokeCount;

    /**
     * 剩余调用次数
     */
    @TableField(value = "left_count")
    private Integer leftCount;

    /**
     * 状态（0-正常，1-禁用）
     */
    @TableField(value = "status")
    private Integer status;

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