package com.example.monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
    private static final String[] WORKERS = {"app1", "app2"};
    
    @Scheduled(fixedRate = 30000) // Runs every 30 seconds
    public void checkWorkerStatus() {
        for (String worker : WORKERS) {
            if (isContainerRunning(worker)) {
                logger.info("Worker {} is UP", worker);
            } else {
                logger.error("Worker {} is DOWN", worker);
                notifyQueue(worker);
            }
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

    private void notifyQueue(String worker) {
        try {
            String content = "Worker down: " + worker;
            int personId = 999;
            int queueId = 999;

            String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8);

            String targetWorker = getFirstAvailableWorker();
            if (targetWorker == null) {
                logger.error("No available workers to handle requests!");
                return;
            }

            String requestUrl = "http://" + targetWorker + ":8080/api/messages"
                    + "?content=" + encodedContent
                    + "&personId=" + personId
                    + "&queueId=" + queueId;

            logger.info("Sending request to: {}", requestUrl);

            new RestTemplate().postForObject(requestUrl, null, String.class);
            logger.info("Alert sent to queue: {}", content);
        } catch (Exception e) {
            logger.error("Failed to send alert to queue", e);
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
