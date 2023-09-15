package com.weeds.openapi.common.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 只含id的请求
 *
 * @author <a href="https://github.com/weedsx">weeds</a>
 */
@Data
public class IdRequest implements Serializable {
    private static final long serialVersionUID = 8257497064465728383L;
    /**
     * id
     */
    private Long id;
}