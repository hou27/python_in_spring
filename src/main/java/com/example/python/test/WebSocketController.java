package com.example.python.test;

import jakarta.annotation.PostConstruct;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class WebSocketController extends TextWebSocketHandler {
    private WebSocketSession session1;

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException, IOException {
        WebSocketClient client = new StandardWebSocketClient();
        session1 = client.execute(this, "ws://localhost:8000/testws").get();
        session1.sendMessage(new TextMessage("Hi Fast API"));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Connected ... " + session.getId());
        try {
            session.sendMessage(new TextMessage("From Spring"));
//            session1.sendMessage(new TextMessage("Hi Fast API"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message: " + message.getPayload());
    }
//    private final TestService testService;
//
//    public WebSocketController(TestService testService) {
//        this.testService = testService;
//    }
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        System.out.println("Connected ... " + session.getId());
//        session.sendMessage(new TextMessage("Hello from Spring!"));
//        this.testService.websocket();
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        System.out.println("Received message: " + message.getPayload());
//    }
}
