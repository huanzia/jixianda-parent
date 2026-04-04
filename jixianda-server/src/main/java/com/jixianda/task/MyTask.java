package com.jixianda.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j

public class MyTask {
//    @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask(){
        log.info("开始执行定时任务");
    }
}
