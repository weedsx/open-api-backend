package com.weeds.openapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weeds.openapi.common.common.ErrorCode;
import com.weeds.openapi.common.exception.BusinessException;
import com.weeds.openapi.common.exception.ThrowUtils;
import com.weeds.openapi.common.model.entity.InterfaceInfo;
import com.weeds.openapi.common.model.enums.InterfaceMethodEnum;
import com.weeds.openapi.mapper.InterfaceInfoMapper;
import com.weeds.openapi.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author weeds
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-07-10 11:57:44
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void verifyInterfaceInfo(InterfaceInfo interfaceInfo, boolean verifyNonnullParam) {
        ThrowUtils.throwIf(interfaceInfo == null, ErrorCode.PARAMS_ERROR);
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        if (verifyNonnullParam) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, url, method), ErrorCode.PARAMS_ERROR);
        }
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 50,
                new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称过长"));
        ThrowUtils.throwIf(StringUtils.isBlank(url),
                new BusinessException(ErrorCode.PARAMS_ERROR, "接口的url不能为空"));
        ThrowUtils.throwIf(!InterfaceMethodEnum.getValueList().contains(method)
                        && !InterfaceMethodEnum.getTextList().contains(method)
                        && !InterfaceMethodEnum.getLowerCaseTextList().contains(method),
                new BusinessException(ErrorCode.PARAMS_ERROR, "接口类型错误"));
        // 接口参数如果通过以上枚举类型的校验，就根据枚举进行赋值
        // 1、如果是小写的方法类型
        InterfaceMethodEnum e = InterfaceMethodEnum.getEnumByLowerCaseText(method);
        if (e != null) {
            interfaceInfo.setMethod(e.getText());
            return;
        }
        // 2、如果是数字
        e = InterfaceMethodEnum.getEnumByValue(method);
        if (e != null) {
            interfaceInfo.setMethod(e.getText());
        }
        // 3、最后一定是大写方法类型
    }
}




