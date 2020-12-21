package com.kuaidao.manageweb.config;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jaegertracing.internal.JaegerSpanContext;
import io.jaegertracing.internal.samplers.RemoteControlledSampler;
import io.opentracing.Span;
import io.opentracing.util.ThreadLocalScopeManager;

@Configuration
public class TracerConfig {
    @Value("${spring.application.name}")
    private String application;
    @Value("${spring.profiles.active}")
    private String active;

    class CustomMDCScopeManager extends ThreadLocalScopeManager {

        @Override
        public Span activeSpan() {
            Span span = super.activeSpan();
            if (null == span || null == span.context()) {
                return null;
            }
            JaegerSpanContext context = (JaegerSpanContext) span.context();
            mdc("traceId", context.getTraceId());
            mdc("spanId", Long.toHexString(context.getSpanId()));
            return span;
        }

        void mdc(String key, String v) {
            if (v == null) {
                MDC.remove(key);
            } else {
                MDC.put(key, v);
            }
        }
    }

    @Bean
    public io.opentracing.Tracer tracer() {
        io.jaegertracing.Configuration config = new io.jaegertracing.Configuration(application +"-" +active);
        io.jaegertracing.Configuration.SenderConfiguration sender = new io.jaegertracing.Configuration.SenderConfiguration();
        sender.withEndpoint("http://tracing-analysis-dc-bj.aliyuncs.com/adapt_f4llc3cjtx@7a8150c3b98dc19_f4llc3cjtx@53df7ad2afe8301/api/traces");
        config.withSampler(new io.jaegertracing.Configuration.SamplerConfiguration().withType(RemoteControlledSampler.TYPE)
                .withManagerHostPort("tracing-analysis-dc-bj.aliyuncs.com/adapt_f4llc3cjtx@7a8150c3b98dc19_f4llc3cjtx@53df7ad2afe8301/api/sampling")
                .withParam(0.1));
        config.withReporter(new io.jaegertracing.Configuration.ReporterConfiguration().withSender(sender).withMaxQueueSize(10000));
        return config.getTracerBuilder().withScopeManager(new CustomMDCScopeManager()).build();
    }
}
