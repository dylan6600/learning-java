package com.ds.chatroom.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CustomStompSessionHandler extends StompSessionHandlerAdapter {
    public CustomStompSessionHandler() {
        super();
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return super.getPayloadType(headers);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        super.handleFrame(headers, payload);
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("StompHeaders: " + connectedHeaders.toString());
//        String destination ="/topic/public";
//
//        session.subscribe(destination, new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return byte[].class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                log.info(new String((byte[]) payload));
//
//            }
//        });
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error(exception.getMessage());
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error(exception.toString());
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.error(e.toString());
        }
    }
}
