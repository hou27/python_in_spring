package com.example.python.test;

//import com.example.python.python.PushupEnsembleModel;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.json.simple.JSONObject;
//import org.python.core.*;
//import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class TestService {
//    public String runPython(JSONObject data) {
//        PushupEnsembleModel pem = new PushupEnsembleModel();
//        ObjectMapper objectMapper = new ObjectMapper();
//        String tmp = (String) data.get("data");
//
//        // JSON 문자열을 ArrayList<HashMap<String, Double>> 형태로 변환
//        ArrayList<HashMap<String, Object>> parsedData;
//        try {
//            parsedData = objectMapper.readValue(tmp, new TypeReference<ArrayList<HashMap<String, Object>>>(){});
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Data parsing error.";
//        }
//
//        ArrayList<HashMap<String, Double>> filteredData = new ArrayList<>();
//        for (HashMap<String, Object> item : parsedData) {
//            item.remove("name");
//            HashMap<String, Double> newItem = new HashMap<>();
//            for (Map.Entry<String, Object> entry : item.entrySet()) {
//                newItem.put(entry.getKey(), Double.valueOf(entry.getValue().toString()));
//            }
//            filteredData.add(newItem);
//        }
//
//        return pem.predict(filteredData).toString();
//    }

    public void websocket() {
        StandardWebSocketClient client = new StandardWebSocketClient();

        try {
            WebSocketSession session = client.execute(new MyHandler(), "ws://localhost:8000/testws").get();

            TextMessage message = new TextMessage("From Spring");
            session.sendMessage(message);

            Thread.sleep(10000);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    static class MyHandler extends TextWebSocketHandler {
        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) {
            System.out.println("Received message: " + message.getPayload());
        }
    }
}