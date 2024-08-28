package com.ds.chatroom.client;

import com.ds.chatroom.domain.InMessage;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
//@Component
public class StompClientThreadManager {
    private Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final static String URL = "http://127.0.0.1:8080/endpoint-websocket";
    private final static String TOKEN = "token111";
    private final static String TOPIC = "/topic/stock";
    private static StompSession stompSession; //全局session

    @PostConstruct
    public void init() {
        startThread();
    }

    /*
    线程启动
     */
    @SneakyThrows
    private void startThread() {
        thread = new Thread(() -> {
            running.set(true);
            try {
                //业务
                while (true) {
                    if (stompSession == null || !stompSession.isConnected()) {
                        log.info("尝试连接中...");
                        connect();
                    }
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                running.set(false);
            }
        });
        TimeUnit.SECONDS.sleep(10);
        thread.start();
    }

    @Scheduled(fixedRate = 10 * 1000) //10s检查
    public void checkAndRestartThread() {
        if (!running.get() && (thread == null || !thread.isAlive())) {
            startThread(); //线程重启
        }
    }


    public void connect() {
        if (stompSession == null || !stompSession.isConnected()) {
            log.info("当前处于断开状态，尝试连接...");
            List<Transport> transports = new ArrayList<>();
            transports.add(new WebSocketTransport(new StandardWebSocketClient()));
            SockJsClient transport = new SockJsClient(transports);
            transport.setMessageCodec(new Jackson2SockJsMessageCodec());
            WebSocketStompClient stompClient = new WebSocketStompClient(transport);

            stompClient.setInboundMessageSizeLimit(1024 * 1024);
            ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.afterPropertiesSet();
            stompClient.setTaskScheduler(taskScheduler);

            StompSessionHandler customHandler = new CustomStompSessionHandler();
            StompHeaders stompHeaders = new StompHeaders();
            stompHeaders.add("TOKEN", TOKEN);
            try {
                ListenableFuture<StompSession> future = stompClient.connect(URI.create(URL), null, stompHeaders, customHandler);
                stompSession = future.get();
                stompSession.setAutoReceipt(true);
                stompSession.subscribe(TOPIC, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return byte[].class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        //消息处理
                        log.info("接收到的消息：" + new String((byte[]) payload));
                    }
                });

                if (stompSession.isConnected()) {
                    log.info("send test");
                    stompSession.send("/app/v2/schedule/push", new InMessage("test").toString().getBytes());
                    log.info("连接成功");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("当前处于连接状态");
        }
    }
}
