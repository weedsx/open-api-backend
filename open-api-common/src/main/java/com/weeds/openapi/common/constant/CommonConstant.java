package com.weeds.openapi.common.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 通用常量
 *
 * @author <a href="https://github.com/weedsx">weeds</a>
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    /**
     * 带有请求体的请求类型
     */
    List<String> METHOD_TYPE_WITH_BODY = Arrays.asList("POST", "PUT", "PATCH", "DELETE",
            "post", "put", "patch", "delete");

}
