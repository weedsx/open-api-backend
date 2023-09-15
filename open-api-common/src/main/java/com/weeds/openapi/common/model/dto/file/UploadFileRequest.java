package com.weeds.openapi.common.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author <a href="https://github.com/weedsx">weeds</a>
 */
@Data
public class UploadFileRequest implements Serializable {

    private static final long serialVersionUID = -972280862435579344L;
    /**
     * 业务
     */
    private String biz;
}