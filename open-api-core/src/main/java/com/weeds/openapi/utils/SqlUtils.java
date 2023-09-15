package com.weeds.openapi.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 工具
 *
 * @author <a href="https://github.com/weedsx">weeds</a>
 */
public class SqlUtils {
    private static final Pattern PATTERN = Pattern.compile(
            "\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|'|%)");

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }

        Matcher matcher = PATTERN.matcher(sortField);
        return matcher.find();
    }
}
