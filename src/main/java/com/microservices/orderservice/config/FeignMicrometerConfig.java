package com.microservices.orderservice.config;

import feign.Capability;
import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignMicrometerConfig {

    @Bean
    public Capability capability(final MeterRegistry meterRegistry) {
        return new MicrometerCapability(meterRegistry);
    }
}
