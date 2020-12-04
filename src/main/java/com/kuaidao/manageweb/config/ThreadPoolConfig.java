package com.kuaidao.manageweb.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author admin
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    @SuppressWarnings("all")
    @Bean("threadPoolExecutor")
    public Executor threadPoolExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(30);
        // 最大线程池数
        executor.setMaxPoolSize(100);
        // 阻塞队列大小
        executor.setQueueCapacity(2000);
        // 空闲时间
        executor.setKeepAliveSeconds(60);
        // 线程池名称前缀
        executor.setThreadNamePrefix("threadPoolExecutor-");
        // 饱和策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy(){
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                if (!e.isShutdown()) {
                    log.warn("线程池饱和!");
                    r.run();
                }
            }
        });
        // 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}
