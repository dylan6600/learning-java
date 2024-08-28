package com.ds.chatroom.controller;

import com.ds.chatroom.domain.InMessage;
import com.ds.chatroom.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;


@Controller
@Slf4j
public class GameInfoController {

    @Autowired
    private WebSocketService webSocketService;

//    @MessageMapping("/v1/chat")
//    @SendTo("/topic/game_chat")
//    public OutMessage gameInfo(InMessage message) throws InterruptedException {
//        log.info("receive: {}", message.getContent());
//        webSocketService.sendTopicMessage("/topic/game_rank", message);
//        return new OutMessage(message.getContent());
//    }

    @MessageMapping("/v1/chat")
    @SendTo("/topic/game_chat")
    public void gameInfo(InMessage message) throws InterruptedException {
        log.info("receive: {}", message.getContent());
        webSocketService.sendTopicMessage("/topic/game_chat", message);
    }

    @MessageMapping("/v1/schedule/push")
    @Scheduled(fixedRate = 3000)  //方法不能加参数
    public void sendServerInfo() {
        webSocketService.sendServerInfo();
    }

    @Scheduled(fixedRate = 3000)
    @MessageMapping("/v2/schedule/push")
    public void sendStock() {
        webSocketService.sendStock("sh600519");
    }


}



