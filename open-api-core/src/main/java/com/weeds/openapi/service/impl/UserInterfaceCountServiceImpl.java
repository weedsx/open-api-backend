package com.weeds.openapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weeds.openapi.common.common.ErrorCode;
import com.weeds.openapi.common.exception.ThrowUtils;
import com.weeds.openapi.common.model.entity.InterfaceInfo;
import com.weeds.openapi.common.model.entity.UserInterfaceCount;
import com.weeds.openapi.common.model.enums.InterfaceStatusEnum;
import com.weeds.openapi.constant.RedisKeyConstant;
import com.weeds.openapi.mapper.UserInterfaceCountMapper;
import com.weeds.openapi.service.InterfaceInfoService;
import com.weeds.openapi.service.UserInterfaceCountService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author weeds
 * @description 针对表【user_interface_count(用户接口关系表)】的数据库操作Service实现
 * @createDate 2023-07-25 15:01:31
 */
@Service
public class UserInterfaceCountServiceImpl extends ServiceImpl<UserInterfaceCountMapper, UserInterfaceCount>
        implements UserInterfaceCountService {
    public static final Integer INIT_LEFT_COUNT = 100;
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void verifyUserInterface(UserInterfaceCount userInterfaceCount, boolean verifyNonnullParam) {
        ThrowUtils.throwIf(userInterfaceCount == null, ErrorCode.PARAMS_ERROR);
        Long userId = userInterfaceCount.getUserId();
        Long interfaceId = userInterfaceCount.getInterfaceId();
        Integer invokeCount = userInterfaceCount.getInvokeCount();
        Integer leftCount = userInterfaceCount.getLeftCount();
        if (verifyNonnullParam) {
            ThrowUtils.throwIf(userId == null || interfaceId == null, ErrorCode.PARAMS_ERROR);
        }
        // 对接口进行校验
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        // 1、看接口存不存在
        ThrowUtils.throwIf(interfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 2、看接口状态正不正常
        ThrowUtils.throwIf(InterfaceStatusEnum.OFFLINE.equals(InterfaceStatusEnum.getEnumByValue(interfaceInfo.getStatus())),
                ErrorCode.FORBIDDEN_ERROR, "接口已下线");
        // 3、调用次数和剩余次数不能小于0
        ThrowUtils.throwIf(invokeCount != null && invokeCount < 0,
                ErrorCode.PARAMS_ERROR, "调用次数非法");
        ThrowUtils.throwIf(leftCount != null && leftCount < 0,
                ErrorCode.PARAMS_ERROR, "剩余次数非法");
    }

    @Override
    public boolean increaseInvokeCountByOne(long interfaceId, long userId) {
        ThrowUtils.throwIf(interfaceId <= 0 || userId <= 0, ErrorCode.PARAMS_ERROR);
        // 先查询剩余次数是否充足
        LambdaUpdateWrapper<UserInterfaceCount> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserInterfaceCount::getUserId, userId)
                .eq(UserInterfaceCount::getInterfaceId, interfaceId);
        UserInterfaceCount userInterfaceCount = this.getOne(wrapper);
        ThrowUtils.throwIf(userInterfaceCount == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(userInterfaceCount.getLeftCount() <= 0, ErrorCode.OPERATION_ERROR, "剩余次数不足");
        // 分布式锁
        RLock lock = redissonClient.getLock(RedisKeyConstant.INCREASE_INVOKE_COUNT_BY_ONE);
        try {
            while (true) {
                // 多机环境下只有一个机器的定时任务线程能执行
                if (lock.tryLock(0, TimeUnit.MILLISECONDS)) {
                    // 需要上锁的操作
                    wrapper = new LambdaUpdateWrapper<>();
                    wrapper.eq(UserInterfaceCount::getUserId, userId)
                            .eq(UserInterfaceCount::getInterfaceId, interfaceId)
                            .setSql("invoke_count = invoke_count + 1, left_count = left_count - 1");
                    return this.update(wrapper);
                }
            }
        } catch (InterruptedException e) {
            log.error("increaseInvokeCountByOne error: ", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    public boolean initUserInterface(Long userId, Long interfaceId) {
        ThrowUtils.throwIf(userId == null || interfaceId == null, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<UserInterfaceCount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterfaceCount::getUserId, userId)
                .eq(UserInterfaceCount::getInterfaceId, interfaceId);
        long count = this.count(wrapper);
        if (count == 0) {
            UserInterfaceCount userInterfaceCount = new UserInterfaceCount() {{
                setUserId(userId);
                setInterfaceId(interfaceId);
                setLeftCount(INIT_LEFT_COUNT);
            }};
            return this.save(userInterfaceCount);
        }
        return true;
    }
}




