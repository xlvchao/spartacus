package com.xlc.spartacus.gateway.config;

import java.util.List;
import java.util.stream.Stream;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 参考 DefaultBlockRequestHandler
 *
 * @author xlc, since 2021
 */
public class MyBlockRequestHandler implements BlockRequestHandler {

    public MyBlockRequestHandler() {
    }

    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable ex) {
//        return this.acceptsHtml(exchange) ? this.htmlErrorResponse(ex) : ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).contentType(MediaType.APPLICATION_JSON_UTF8).body(BodyInserters.fromObject(this.buildErrorResult(ex, exchange.getRequest().getURI().getPath())));
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).contentType(MediaType.APPLICATION_JSON_UTF8).body(BodyInserters.fromObject(this.buildErrorResult(ex, exchange.getRequest().getURI().getPath())));
    }

    private Mono<ServerResponse> htmlErrorResponse(Throwable ex) {
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).contentType(MediaType.TEXT_PLAIN).syncBody("Blocked by Spartacus: " + ex.getClass().getSimpleName());
    }

    private MyBlockRequestHandler.ErrorResult buildErrorResult(Throwable ex, String rote) {
        return new MyBlockRequestHandler.ErrorResult(HttpStatus.TOO_MANY_REQUESTS.value(), "Blocked by Spartacus: Too Many Requests!", rote);
    }

    private boolean acceptsHtml(ServerWebExchange exchange) {
        try {
            List<MediaType> acceptedMediaTypes = exchange.getRequest().getHeaders().getAccept();
            acceptedMediaTypes.remove(MediaType.ALL);
            MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
            Stream var10000 = acceptedMediaTypes.stream();
            MediaType var10001 = MediaType.TEXT_HTML;
            var10001.getClass();
            return var10000.anyMatch(var -> var10001.isCompatibleWith((MediaType) var));
        } catch (InvalidMediaTypeException var3) {
            return false;
        }
    }

    private static class ErrorResult {
        private final int code;
        private final String message;
        private final String rote;

        ErrorResult(int code, String message, String rote) {
            this.code = code;
            this.message = message;
            this.rote = rote;
        }

        public int getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }

        public String getRote() {
            return rote;
        }
    }
}