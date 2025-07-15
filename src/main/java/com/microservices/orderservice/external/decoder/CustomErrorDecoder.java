package com.microservices.orderservice.external.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.orderservice.exception.OrderServiceCustomException;
import com.microservices.orderservice.model.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("::{}", response.request().url());
        log.info("::{}", response.request().headers());

        try {
            InputStream inputStream = response.body().asInputStream();
            ErrorResponse errorResponse = objectMapper.readValue(inputStream, ErrorResponse.class);
            throw new OrderServiceCustomException(errorResponse.getErrorMessage(), errorResponse.getErrorCode(), errorResponse.getStatus().value());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
