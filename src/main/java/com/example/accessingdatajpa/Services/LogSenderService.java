package com.example.accessingdatajpa.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LogSenderService {
    private static final Logger logger = LoggerFactory.getLogger(LogSenderService.class);
    private RestTemplate restTemplate = new RestTemplate();

    public void sendLog(String logMessage) {
        try {
            String url = "http://localhost:80/log";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> payload = new HashMap<>();
            payload.put("log", logMessage);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(url, request, String.class);
            logger.info("Log message sent to external application: {}", logMessage);
        } catch (Exception e) {
            // On capture l'exception et on la log sans la relancer pour éviter toute boucle infinie.
            logger.error("Failed to send log message to external application: {}", e.getMessage());
        }
    }
}
