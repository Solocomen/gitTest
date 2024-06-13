package epc.epcsalesapi.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
//	@Bean
//	public Executor asyncExecutor() {
//	   ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//	   executor.setCorePoolSize(1000);
//	   executor.setMaxPoolSize(1000);
//	   executor.setQueueCapacity(1000);
//	   executor.setThreadNamePrefix("Async-");
//	   executor.initialize();
//	   return executor;
//	}
}
