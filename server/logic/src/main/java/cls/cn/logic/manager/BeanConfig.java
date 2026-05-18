package cls.cn.logic.manager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BeanConfig {

    @Bean("dbSave")
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        //TODO 后期有需要再做多线程改造 目前单线程
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("dbSave-");
        executor.initialize();
        return executor;
    }

}