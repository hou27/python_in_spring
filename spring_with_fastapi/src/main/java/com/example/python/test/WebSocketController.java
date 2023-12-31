package com.example.python.test;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketController extends TextWebSocketHandler {
    private final TestService testService;

    public WebSocketController(TestService testService) {
        this.testService = testService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connected ... " + session.getId());
        session.sendMessage(new TextMessage("Hello from Spring!"));
//        this.testService.countPushup();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message: " + message.getPayload());
        this.testService.countPushup(message.getPayload());
    }
}
