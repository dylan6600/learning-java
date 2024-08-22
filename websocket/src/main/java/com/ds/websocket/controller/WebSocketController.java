package com.ds.websocket.controller;

import com.ds.websocket.message.WebSocket;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/websocket")
public class WebSocketController {
    @Autowired
    private WebSocket webSocket;

    @Operation(tags = "发送广播消息")
    @PostMapping("/broadcast/send")
    public Object sendBroadcastMessage(@RequestParam String message) throws IOException {
        webSocket.broadCast(message);
        return "success";
    }

    @Operation( tags = "发送单点消息")
    @PostMapping("/single/send")
    public Object sendSingleMessage(@RequestParam String userId, @RequestParam String message) throws IOException {
        webSocket.sendMessage(userId, message);
        return "success";
    }
}
