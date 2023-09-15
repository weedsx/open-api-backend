package com.weeds.openapi.common.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口方法枚举
 *
 * @author weeds
 */
@Getter
public enum InterfaceMethodEnum {
    GET("GET", "get", "0"),
    POST("POST", "post", "1"),
    PUT("PUT", "put", "2"),
    DELETE("DELETE", "delete", "3");
    private final String text;
    private final String lowerCaseText;
    private final String value;

    /**
     * 获取枚举的所有value值
     *
     * @return
     */
    public static List<String> getValueList() {
        return Arrays.stream(values())
                .map(InterfaceMethodEnum::getValue)
                .collect(Collectors.toList());
    }

    /**
     * 获取枚举的所有text值
     *
     * @return
     */
    public static List<String> getTextList() {
        return Arrays.stream(values())
                .map(InterfaceMethodEnum::getText)
                .collect(Collectors.toList());
    }

    /**
     * 获取枚举的所有lowerCaseText值
     *
     * @return
     */
    public static List<String> getLowerCaseTextList() {
        return Arrays.stream(values())
                .map(InterfaceMethodEnum::getLowerCaseText)
                .collect(Collectors.toList());
    }

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static InterfaceMethodEnum getEnumByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (InterfaceMethodEnum interfaceMethodEnum : InterfaceMethodEnum.values()) {
            if (interfaceMethodEnum.value.equals(value)) {
                return interfaceMethodEnum;
            }
        }
        return null;
    }

    /**
     * 根据text获取枚举
     *
     * @param text
     * @return
     */
    public static InterfaceMethodEnum getEnumByText(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        for (InterfaceMethodEnum interfaceMethodEnum : InterfaceMethodEnum.values()) {
            if (interfaceMethodEnum.text.equals(text)) {
                return interfaceMethodEnum;
            }
        }
        return null;
    }

    /**
     * 根据text获取枚举
     *
     * @param lowerCaseText
     * @return
     */
    public static InterfaceMethodEnum getEnumByLowerCaseText(String lowerCaseText) {
        if (StringUtils.isBlank(lowerCaseText)) {
            return null;
        }
        for (InterfaceMethodEnum interfaceMethodEnum : InterfaceMethodEnum.values()) {
            if (interfaceMethodEnum.lowerCaseText.equals(lowerCaseText)) {
                return interfaceMethodEnum;
            }
        }
        return null;
    }

    InterfaceMethodEnum(String text, String lowerCaseText, String value) {
        this.text = text;
        this.lowerCaseText = lowerCaseText;
        this.value = value;
    }

}
