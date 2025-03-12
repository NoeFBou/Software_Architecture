package com.example.monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
    private static final String[] WORKERS = {"software_architecture-app1-1", "software_architecture-app2-1"};

    public MonitoringService() {
    }

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
        String content = "Worker down: " + worker;
        int personId = 999; // Our monitoring will be the person 999
        int queueId = 999; // If the queue is not existing, it will be automatically created

        // Update the request to use Nginx (on port 80)
        String requestUrl = "http://nginx:80/api/messages?content=" + content + "&personId=" + personId + "&queueId=" + queueId;
        logger.info("Sending request to: {}", requestUrl);
        try {
            // Post a message to the queue indicating the worker is down
            new RestTemplate().postForObject(requestUrl, null, String.class);
            logger.info("Alert sent to queue: {}", content);
        } catch (Exception e) {
            logger.error("Failed to send alert to queue", e);
        }
    }
}
