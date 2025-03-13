package com.example.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class ControllerService {

    private static final Logger logger = LoggerFactory.getLogger(ControllerService.class);
    private static final String MESSAGE_APP_SUFFIX = ":8080/api/queues/999/read";
    private static final String[] WORKERS = {"app1", "app2"};

    /**
     * Periodically checks for messages in Queue 999.
     * Sends a PUT request to the MessageApp and checks if the message indicates a worker is down.
     * If a worker is down, logs a critical message.
     */
    @Scheduled(fixedDelay = 5000) // Runs every 5 seconds
    public void checkMessagesInQueue() {
        try {
            String targetWorker = getFirstAvailableWorker();
            if (targetWorker == null) {
                throw new Exception("No worker available");
            }

            String requestUrl = "http://" + targetWorker + MESSAGE_APP_SUFFIX;

            ResponseEntity<String> response = new RestTemplate().getForEntity(requestUrl, String.class);

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

    private boolean isContainerRunning(String containerName) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"docker", "ps", "--format", "{{.Names}}"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.lines().anyMatch(line -> line.contains(containerName));
        } catch (IOException e) {
            return false;
        }
    }

    private String getFirstAvailableWorker() {
        // Dynamically check for available workers from the status of containers at the time of notification
        for (String worker : WORKERS) {
            if (isContainerRunning(worker)) {
                return worker;
            }
        }
        return null; // Return null if no worker is available
    }
}
