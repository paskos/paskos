package org.paskos.test.project;

import lombok.extern.flogger.Flogger;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setResponseStatus;
import static org.springframework.http.HttpHeaders.EXPECT;
import static org.springframework.http.HttpStatus.CONTINUE;

@Component
@Flogger
public class ExpectContinueGatewayFilterFactory extends AbstractGatewayFilterFactory<ExpectContinueGatewayFilterFactory.Config> {

    public static final String CLASS = ExpectContinueGatewayFilterFactory.class.getSimpleName();

    public ExpectContinueGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(ExpectContinueGatewayFilterFactory.Config config) {
        return (exchange, chain) -> {
            val response = exchange.getResponse();
            if (response.isCommitted()) {
                return Mono.empty();
            }
            val request = exchange.getRequest();
            val headers = request.getHeaders();
            val expect = headers.getFirst(EXPECT);

            if (!StringUtils.equals(expect, "100-continue")) {
                return chain.filter(exchange);
            }

            setResponseStatus(exchange, CONTINUE);
            return response.setComplete();
        };
    }
    public static class Config {

    }
}