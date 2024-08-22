package com.ds.websocket.message;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WebSocketImpl implements WebSocket {
    /**
     * 在线连接数（线程安全）
     */
    private final AtomicInteger connectionCount = new AtomicInteger(0);
    private final int IPConnectMax = 5;

    /**
     * 线程安全的无序集合（存储会话）
     */
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @SneakyThrows
    @Override
    public void handleOpen(WebSocketSession session) {

        String ip = session.getRemoteAddress().getHostString();
        long ipCount = sessions.stream().filter(s -> (s.getRemoteAddress().getHostString().equals(ip))).count();
        if (ipCount < IPConnectMax) {
            sessions.add(session);
            int count = connectionCount.incrementAndGet();
            log.info("a new connection opened: {} , current online count：{}", ip, count);
        } else {
            session.close();
            log.info("IP：{} 连接数超过 {}", ip, IPConnectMax);
        }
    }

    @Override
    public void handleClose(WebSocketSession session) {
        sessions.remove(session);
        int count = connectionCount.decrementAndGet();
        String ip = session.getRemoteAddress().getHostString();
        log.info("a new connection closed: {} ,current online count：{}", ip, count);
    }

    @Override
    public void handleMessage(WebSocketSession session, String message) {
        // 只处理前端传来的文本消息，并且直接丢弃了客户端传来的消息
        log.info("received a message：{}", message);
    }

    @Override
    public void sendMessage(WebSocketSession session, String message) throws IOException {
        this.sendMessage(session, new TextMessage(message));
    }

    @Override
    public void sendMessage(String userId, TextMessage message) throws IOException {
//        Optional<WebSocketSession> userSession = sessions.stream().filter(session -> {
//            if (!session.isOpen()) {
//                return false;
//            }
//            Map<String, Object> attributes = session.getAttributes();
//            if (!attributes.containsKey(WebSocketConstant.USER_KEY)) {
//                return false;
//            }
//            UserDO user = (UserDO) attributes.get(WebSocketConstant.USER_KEY);
//            return user.getId().equals(userId);
//        }).findFirst();
//        if (userSession.isPresent()) {
//            userSession.get().sendMessage(message);
//        }
    }

    @Override
    public void sendMessage(String userId, String message) throws IOException {
        this.sendMessage(userId, new TextMessage(message));
    }

    @Override
    public void sendMessage(WebSocketSession session, TextMessage message) throws IOException {
        session.sendMessage(message);
    }

    @Override
    public void broadCast(String message) throws IOException {
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                continue;
            }
            this.sendMessage(session, message);
        }
    }

    @Override
    public void broadCast(TextMessage message) throws IOException {
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                continue;
            }
            session.sendMessage(message);
        }
    }

    @Override
    public void handleError(WebSocketSession session, Throwable error) {
        log.error("websocket error：{}，session id：{}", error.getMessage(), session.getId());
        log.error("", error);
    }

    @Override
    public Set<WebSocketSession> getSessions() {
        return sessions;
    }

    @Override
    public int getConnectionCount() {
        return connectionCount.get();
    }
}
