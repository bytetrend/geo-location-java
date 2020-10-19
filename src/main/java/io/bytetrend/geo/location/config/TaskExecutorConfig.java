package io.bytetrend.geo.location.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class TaskExecutorConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(TaskExecutorConfig.class);
    @Value("${location.executor.corePoolSize}")
    private int corePoolSize;
    @Value("${location.executor.maxPoolSize}")
    private int maxPoolSize;
    @Value("${location.executor.queueCapacity}")
    private int queueCapacity;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        LOGGER.info("Building task executor with parameters core pool size {} max pool size {} queue capacity {}",
                corePoolSize, maxPoolSize, queueCapacity);
        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadNamePrefix("Global-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
