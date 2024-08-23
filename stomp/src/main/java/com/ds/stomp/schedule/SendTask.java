package com.ds.stomp.schedule;

import cn.hutool.core.date.DateUtil;
import com.ds.stomp.pojo.Greeting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
@Slf4j
public class SendTask {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

//    @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(fixedRate = 10000) //10秒执行一次
    public void schedule() {
        messagingTemplate.convertAndSend("/topic/greetings",new Greeting("Hello, everyone! " + DateUtil.now()));
        log.info("send to /topic/greetings");
    }

}
