package sesac.server.common.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "threadPoolTaskExecutor")            // 비동기 작업에 필요한 경우, @Async, name으로 호출
    public Executor threadPoolTaskExecutor() {        // 설정한 ThreadPoolTaskExecutor 객체를 반환
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);                  // 항상 활성 상태로 유지되는 스레드 개수
        executor.setMaxPoolSize(5);                   // 최대 스레드 개수
        executor.setQueueCapacity(100);               // 작업 대기 큐의 최대  크기
        executor.setThreadNamePrefix("AsyncThread-"); // 생성되는 스레드의 이름 접두사
        executor.initialize();
        return executor;
    }
}