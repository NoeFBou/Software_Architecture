
package com.example.monitoring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class MonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
    private static final String[] WORKERS = {"software_architecture-app1-1", "software_architecture-app2-1"};

    public MonitoringService() {
    }

    @Scheduled(fixedRate = 10000) // Runs every 10 seconds
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
            // Run Docker command to check if container is running
            Process process = Runtime.getRuntime().exec("docker ps --filter 'name=" + containerName + "' --format '{{.Names}}'");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String containerStatus = reader.readLine(); // If the container is running, this will be the container name

            // If the container name matches, it is running
            return containerStatus != null && containerStatus.equals(containerName);
        } catch (Exception e) {
            logger.error("Error checking status of container {}", containerName, e);
            return false;
        }
    }

    private void notifyQueue(String worker) {
        String content = "Worker down: " + worker;
        int personId = 999; // Dummy ID for monitoring messages
        int queueId = 999; // Assuming a queue exists

        String requestUrl = "http://localhost:80/api/messages?content=" + content + "&personId=" + personId + "&queueId=" + queueId;
        try {
            // Post a message to the queue indicating the worker is down
            new RestTemplate().postForObject(requestUrl, null, String.class);
            logger.info("Alert sent to queue: {}", content);
        } catch (Exception e) {
            logger.error("Failed to send alert to queue", e);
        }
    }
}
