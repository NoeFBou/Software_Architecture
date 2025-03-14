package com.example.accessingdatajpa.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class LogSenderService {
    private static final Logger logger = LoggerFactory.getLogger(LogSenderService.class);
    private RestTemplate restTemplate = new RestTemplate();

    public void sendLog(String logMessage) {
        try {
            // Encodage du contenu pour l'inclure dans l'URL
            String encodedMessage = URLEncoder.encode(logMessage, StandardCharsets.UTF_8.toString());
            // Construction de l'URL avec les paramètres requis
            String url = "http://localhost:8080/api/messages?content=" + encodedMessage
                    + "&personId=0&topicNames=log";

            HttpHeaders headers = new HttpHeaders();
            // Ici on garde JSON, mais comme on envoie les paramètres dans l'URL, aucun body n'est nécessaire.
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(null, headers);
            restTemplate.postForEntity(url, request, String.class);
            logger.info("Log message sent as internal message: {}", logMessage);
        } catch (Exception e) {
            // En cas d'erreur, logguez l'exception sans relancer pour éviter la boucle infinie
            logger.error("Failed to send internal log message: {}", e.getMessage());
        }
    }
}
