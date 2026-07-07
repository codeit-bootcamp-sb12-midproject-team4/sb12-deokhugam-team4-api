package com.codeit.deokhugam.global.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig { // Spring Event기반 Threadpool 관리설정

	@Bean(name = "esSyncExecutor")
	public Executor esSyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(3);
		executor.setQueueCapacity(50);
		executor.setMaxPoolSize(10);
		executor.setThreadNamePrefix("es-sync-thread-");
		executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

		executor.initialize();
		return executor;
	}

}
