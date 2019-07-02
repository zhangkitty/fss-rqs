package com.znv.fssrqs.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component("messageHandler")
public class MessageWebSocketHandler implements WebSocketHandler {
    private static final String WEB_SOCKET_URL = "/websocket";

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(arg0 -> {
            
            new WebSocketAsSession(session).setSubscriber(arg0);
        });
    }

    public String getMapping() {
        return WEB_SOCKET_URL;
    }
}
