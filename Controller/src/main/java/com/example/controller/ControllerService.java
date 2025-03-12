package com.example.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class ControllerService {

    private static final Logger logger = LoggerFactory.getLogger(ControllerService.class);
    private static final String MESSAGE_APP_URL = "http://localhost:8080/api/queues/999/read";

    /**
     * Periodically checks for messages in Queue 999.
     * Sends a PUT request to the MessageApp and checks if the message indicates a worker is down.
     * If a worker is down, logs a critical message.
     */
    @Scheduled(fixedDelay = 5000) // Runs every 5 seconds
    public void checkMessagesInQueue() {
        try {
            ResponseEntity<String> response = new RestTemplate().getForEntity(MESSAGE_APP_URL, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String message = response.getBody();

                logger.info("Received message: {}", message);

                if (message.contains("Worker") && message.contains("is DOWN")) {
                    logger.error("CRITICAL: {}", message);
                    // Additional logic can be added to trigger a worker restart mechanism
                } else {
                    logger.info("Other type of message read in Queue 999: {}", message);
                }

            } else {
                logger.warn("Failed to fetch messages from queue 999. Status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error while checking messages in Queue 999", e);
        }
    }
}
