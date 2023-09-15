package com.weeds.gateway.filter;

import com.weeds.dubboapi.service.InnerUserInterfaceCountService;
import com.weeds.gateway.constant.RequestHeaderConstant;
import com.weeds.gateway.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局响应过滤器
 *
 * @author weeds
 */
@Configuration
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ResponseGlobalFilter implements GlobalFilter {
    @DubboReference
    private InnerUserInterfaceCountService innerUserInterfaceCountService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LogUtils.handlePreRequestLog(exchange);
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    LogUtils.handlePreResponseLog(exchange, request);
                    // 7.调用成功，接口调用次数+1
                    // response.getStatusCode() 呼应请求过滤器的拦截
                    if (response.getStatusCode() == HttpStatus.OK) {
                        HttpHeaders headers = request.getHeaders();
                        String userId = headers.getFirst(RequestHeaderConstant.USER_ID);
                        String interfaceId = headers.getFirst(RequestHeaderConstant.INTERFACE_ID);
                        if (StringUtils.isAnyBlank(userId, interfaceId)) {
                            return;
                        }
                        try {
                            // 先检查是否初始化
                            innerUserInterfaceCountService.initUserInterfaceCount(
                                    Long.parseLong(interfaceId),
                                    Long.parseLong(userId));
                            // 调用次数减 1
                            innerUserInterfaceCountService.increaseInvokeCount(
                                    Long.parseLong(interfaceId),
                                    Long.parseLong(userId));
                        } catch (Exception e) {
                            log.error("Gateway increaseInvokeCount error", e);
                        }
                    }
                }));
    }
}
