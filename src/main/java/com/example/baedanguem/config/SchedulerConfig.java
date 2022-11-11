package com.example.baedanguem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

// 쓰레드 풀 설정
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    //   쓰레드 풀 갯수 설정 예시
    //   - CPU 처리가 많은 경우 CPU 코어 갯수 N + 1
    //   - I/O 작업이 많은 경우 CPU 코더 갯수 N x 2

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

        int n = Runtime.getRuntime().availableProcessors(); // core 갯수
        threadPool.setPoolSize(n + 1); // 쓰레드 풀 갯수 설정
        threadPool.initialize();       // 쓰레드 풀 초기화

        taskRegistrar.setTaskScheduler(threadPool); // 스케줄러에서 쓰레드풀 사용

    }

}
