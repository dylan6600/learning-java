package com.ds.chatroom.controller;

import cn.hutool.jwt.JWT;
import com.ds.chatroom.domain.InMessage;
import com.ds.chatroom.domain.User;
import com.ds.chatroom.service.WebSocketService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class UserChatController {
    @Autowired
    private WebSocketService webSocketService;

    //模拟数据库用户的数据
    public static Map<String, String> userMap = new HashMap<>();

    static {
        userMap.put("jack", "123");
        userMap.put("mary", "456");
        userMap.put("tom", "789");
        userMap.put("tim", "000");
    }

    public static Map<String, User> onlineUser = new HashMap<>();

    static {
        onlineUser.put("123", new User("admin", "888"));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String userLogin(@RequestParam(value = "username") String username
            , @RequestParam(value = "pwd") String pwd
            , HttpSession session) {
        String password = userMap.get(username);
        if (pwd.equals(password)) {
            User user = new User(username, pwd);
            String sessionId = session.getId();
            onlineUser.put(sessionId, user);
            return "redirect:/user/chat.html";
        } else {
            return "redirect:/user/error.html";
        }
    }

    @RequestMapping(value = "/login-jwt", method = RequestMethod.POST)
    public String userLogin2(@RequestParam(value = "username") String username
            , @RequestParam(value = "pwd") String pwd
            , HttpSession session) {
        String password = userMap.get(username);
        if (pwd.equals(password)) {
            User user = new User(username, pwd);
            String sessionId = session.getId();
            onlineUser.put(sessionId, user);

            byte[] key = "1234567890".getBytes();
            String token = JWT.create()
                    .setPayload("username", username)
                    .setPayload("admin", false)
                    .setKey(key)
                    .sign();
            log.info(token);
            return "redirect:/user-jwt/chat.html?token="+token;
        } else {
            return "redirect:/user-jwt/error.html";
        }
    }

    @Scheduled(fixedRate = 3000)
    public void sendUserOnline() {
        webSocketService.sendOnlineUser(onlineUser);
    }

    /**
     * 聊天接口
     *
     * @param message
     * @param headerAccessor
     */
    @MessageMapping("/user/chat")
    public void topicChat(InMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        User user = onlineUser.get(sessionId);
        message.setFrom(user.getUsername());
        webSocketService.sendTopicChat(message);
    }

}
