package com.weeds.gateway.utils;

import com.weeds.gateway.constant.RequestHeaderConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 日志工具类
 *
 * @author weeds
 */
@Slf4j
public class LogUtils {
    private LogUtils() {
    }

    /**
     * 请求前置的打印日志
     *
     * @param exchange
     * @return
     */
    public static ServerHttpRequest handlePreRequestLog(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        // 打印请求路径
        String path = request.getPath().pathWithinApplication().value();
        // 打印请求url
        String requestUrl = getOriginalRequestUrl(exchange);

        // 构建成一条长日志，避免并发下日志错乱
        StringBuilder reqLog = new StringBuilder(200);
        // 日志参数
        List<Object> reqArgs = new ArrayList<>();
        reqLog.append("\n\n================  Gateway Request Start  ================\n");
        // 打印路由添加占位符
        reqLog.append("===> {}: {} ({})\n").append("===RequestHeaders===\n");
        // 参数
        reqArgs.add(request.getMethodValue());
        reqArgs.add(requestUrl);
        reqArgs.add(path);

        // 打印请求头
        HttpHeaders headers = request.getHeaders();
        headers.forEach((headerName, headerValue) -> {
            reqLog.append("{}: {}\n");
            reqArgs.add(headerName);
            reqArgs.add(StringUtils.join(headerValue, ","));
        });
        reqLog.append("================  Gateway Request End  =================\n");

        log.info(reqLog.toString(), reqArgs.toArray());
        return request;
    }

    /**
     * 响应前置的打印日志
     *
     * @param exchange
     * @return
     */
    public static void handlePreResponseLog(ServerWebExchange exchange, ServerHttpRequest request) {
        // 记录请求处理时长
        String timeStamp = request.getHeaders().getFirst(RequestHeaderConstant.TIME_STAMP);
        long duration = timeStamp == null ? 0 : System.currentTimeMillis() - Long.parseLong(timeStamp);
        // 打印请求路径
        String path = request.getPath().pathWithinApplication().value();

        MultiValueMap<String, String> queryParams = request.getQueryParams();
        String requestUrl = UriComponentsBuilder.fromPath(path).queryParams(queryParams).build().toUriString();

        // 构建成一条长日志
        StringBuilder responseLog = new StringBuilder(200);
        // 日志参数
        List<Object> responseArgs = new ArrayList<>();
        responseLog.append("\n\n================  Gateway Response Start  ================\n")
                .append("Processing Time: {} ms\n");
        ServerHttpResponse response = exchange.getResponse();
        // 状态码个path占位符: 200 get: /xxx/xxx/xxx?a=b
        responseLog.append("<=== {} {}: {}\n")
                .append("===ResponseHeaders===\n");
        // 参数
        String requestMethod = request.getMethodValue();
        responseArgs.add(duration);
        responseArgs.add(Objects.requireNonNull(response.getStatusCode()).value());
        responseArgs.add(requestMethod);
        responseArgs.add(requestUrl);

        // 打印响应头
        HttpHeaders headers = response.getHeaders();
        headers.forEach((headerName, headerValue) -> {
            responseLog.append("{}: {}\n");
            responseArgs.add(headerName);
            responseArgs.add(StringUtils.join(headerValue, ","));
        });
        responseLog.append("================  Gateway Response End  =================\n");

        log.info(responseLog.toString(), responseArgs.toArray());
    }

    /**
     * 获取请求 Url
     *
     * @param exchange
     * @return
     */
    private static String getOriginalRequestUrl(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        URI requestUri = request.getURI();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        return UriComponentsBuilder.fromPath(requestUri.getRawPath()).queryParams(queryParams).build().toUriString();
    }
}
