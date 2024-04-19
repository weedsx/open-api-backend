package com.weeds.gateway.filter;

import com.weeds.client.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 网关全局过滤器
 *
 * @author weeds
 */
//@Component
@Slf4j
@Order(-1) // 过滤器优先级，越小优先级越高
@Deprecated // 未采用
public class CustomGlobalFilter implements GlobalFilter {
    /**
     * IP 白名单
     */
    private static final List<String> WHITELIST = Arrays.asList("127.0.0.1", "localhost");
    /**
     * 请求延时时长：5min
     */
    public static final long REQUEST_DELAY_TIME = 60 * 5L;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Void> responseMono = processBusiness(exchange);
        if (responseMono != null) {
            return responseMono;
        }
        return chain.filter(exchange);
    }

    /**
     * 负责打印请求日志、白名单比对、用户鉴权（判新ak、sk是否合法）、接口调用的记录
     *
     * @param exchange
     * @return
     */
    private static Mono<Void> processBusiness(ServerWebExchange exchange) {
        // 1.用户发送请求到AP川网关
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String host = request.getURI().getHost();
        // 2.请求日志
        log.info("=============================Gateway打印日志开始===============================");
        log.info("访问接口host: " + host);
        log.info("访问接口端口: " + request.getURI().getPort());
        log.info("访问接口URL: " + request.getURI().getPath());
        log.info("访问接口URL参数: " + request.getURI().getRawQuery());
        log.info("=============================Gateway打印日志结束===============================");
        // 3.白名单
        if (!WHITELIST.contains(host)) {
            return handleForbiddenResponse(response);
        }
        // 4.用户鉴权（判新ak、sk是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        // todo: 数据库查询比对用户 accessKey
        if (!"weeds".equals(accessKey)) {
            return handleForbiddenResponse(response);
        }
        if (nonce == null || Long.parseLong(nonce) > 10000L) {
            return handleForbiddenResponse(response);
        }
        // 时间和当前时间不能超过 5 分钟
        long currentTime = System.currentTimeMillis() / 1000;
        if (timestamp == null || currentTime - Long.parseLong(timestamp) > REQUEST_DELAY_TIME) {
            return handleForbiddenResponse(response);
        }
        // 校验 secretKey todo: 查询用户的 SecretKey
        String genSign = SignUtils.genSign(body, "weeds");
        if (!genSign.equals(sign)) {
            return handleForbiddenResponse(response);
        }
        // 5.请求的模以接口是否存在？ todo
        // 6.请求转发，调用模拟接口 todo
        // 7.响应日志
        log.info("响应码：" + response.getStatusCode());
        // 8.调用成功，接口调用次数+1 todo
        // 9.调用失败，返回一个规范的错误码
        if (response.getStatusCode() != HttpStatus.OK) {
            return response.setComplete();
        }
        return null;
    }

    public Mono<Void> handleResponseLogGlobalFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                sb2.append("<--- {} {} \n");
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                log.info(sb2.toString(), rspArgs.toArray());
                                //log.info("<-- {} {}\n", originalResponse.getStatusCode(), data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 降级处理返回数据
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }

    /**
     * 处理 FORBIDDEN 响应
     *
     * @param response
     * @return
     */
    private static Mono<Void> handleForbiddenResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        // 设置响应，结束此次请求
        return response.setComplete();
    }
}
