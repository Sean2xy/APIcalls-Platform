package com.yupi.yuapigateway;

import com.yupi.yuapiclientsdk.utils.SignUtils;
import com.yupi.yuapicommon.model.entity.InterfaceInfo;
import com.yupi.yuapicommon.model.entity.User;
import com.yupi.yuapicommon.service.InnerInterfaceInfoService;
import com.yupi.yuapicommon.service.InnerUserInterfaceInfoService;
import com.yupi.yuapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    public static final String INTERFACE_HOST = "http://localhost:8123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

//        业务逻辑:
//        用户发送请求到 API 网关
//        请求日志
        ServerHttpRequest request = exchange.getRequest();

        String path = request.getPath().value();
        String method = request.getMethod().toString();

        log.info("请求唯一标识"+request.getId());
        log.info("请求路径"+request.getPath().value());
        log.info("请求方法"+request.getMethod());
        log.info("请求参数"+request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        ServerHttpResponse response = exchange.getResponse();
        log.info("请求来源地址"+request.getRemoteAddress());
//        （黑白名单）
        if(!IP_WHITE_LIST.contains(sourceAddress)){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
//        用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        // todo
        User invokeUser = null;
       try {
           invokeUser = innerUserService.getInvokeUser(accessKey);
       }catch (Exception e){
           log.error("getInvokeUser error", e);
       }
        if( invokeUser ==null){
            return handleNoAuth(response);
        }

//        if(!accessKey.equals("yupi")){
//            return handleNoAuth(response);
//        }

        if(Long.parseLong(nonce) > 10000L ){
            return handleNoAuth(response);
        }
        Long currentTime = System.currentTimeMillis() / 1000;
        final Long Five_MINUTES = 60 * 5L;
        if(currentTime - Long.parseLong(timestamp) >= Five_MINUTES ){
            return handleNoAuth(response);
        }

        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.getSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)){
            return handleNoAuth(response);
        }

//        请求的模拟接口是否存在？
        // TODO
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path,method);
        }catch (Exception e){
            log.error("getInterfaceInfor error", e);
        }
        if( interfaceInfo == null){
            return handleNoAuth(response);
        }

        int validLeftNum = 0;
        try {
            validLeftNum = innerUserInterfaceInfoService.validLeftNum(interfaceInfo.getId(), invokeUser.getId());
        }catch (Exception e){
            log.error("validLeftNum error", e);
        }
        if( validLeftNum <= 0){
            return handleNoAuth(response);
        }

        return handleResponse(exchange,chain,interfaceInfo.getId(),invokeUser.getId());
//        请求转发，调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);
////                响应日志
//        log.info("响应"+response.getStatusCode());
////        调用成功，接口调用次数 + 1
//        // todo
////        调用失败，返回一个规范的错误码
//        if(response.getStatusCode() == HttpStatus.OK){
//
//        }else {
//            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//            return response.setComplete();
//        }
//
//        return filter;
//
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId){
        try {
            //从交换寄拿响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            //缓冲区工厂，拿到缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if(statusCode == HttpStatus.OK){
                //装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    //等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        //对象是响应式的
                        if (body instanceof Flux) {
                            //我们拿到真正的body
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //往返回值里面写数据
                            //拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 8. 调用成功， todo 接口调用次数+1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }


                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8);//data
                                        sb2.append(data);
                                        //打印日志
                                        log.info("响应结果" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 9. 调用失败，返回规范错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                //设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        }catch (Exception e){
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }

    }

}