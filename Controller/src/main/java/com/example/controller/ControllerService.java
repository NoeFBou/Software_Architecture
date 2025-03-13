package com.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                String messageJson  = response.getBody();

                logger.info("Received message: {}", messageJson );
                ObjectMapper mapper = new ObjectMapper();
                MessageDTO messageDTO = mapper.readValue(messageJson, MessageDTO.class);
                String content = messageDTO.getContent();
                String decodedContent = URLDecoder.decode(content, StandardCharsets.UTF_8);

                if (decodedContent.contains("Worker") && decodedContent.contains("down")) {
                    logger.error("CRITICAL: {}", decodedContent);
// Extraction du nom du worker en panne avec une expression régulière
                    Pattern pattern = Pattern.compile("Worker down: (\\w+)");
                    Matcher matcher = pattern.matcher(decodedContent);
                    if (matcher.find()) {
                        String workerName = matcher.group(1); // ici, workerName vaut "app2"
                        logger.info("Redémarrage du conteneur pour le worker: {}", workerName);

                        // Récupération du nom complet du conteneur à partir du worker
                        String containerName = getContainerNameByWorker(workerName);
                        if (containerName != null) {
                            try {
                                // Exécute la commande Docker pour redémarrer le conteneur
                                Process restartProcess = Runtime.getRuntime().exec(new String[]{"docker", "restart", containerName});

                                // Lecture du flux d'erreur pour obtenir plus d'informations en cas de problème
                                BufferedReader errorReader = new BufferedReader(new InputStreamReader(restartProcess.getErrorStream()));
                                StringBuilder errorOutput = new StringBuilder();
                                String line;
                                while ((line = errorReader.readLine()) != null) {
                                    errorOutput.append(line).append("\n");
                                }

                                int exitCode = restartProcess.waitFor();
                                if (exitCode == 0) {
                                    logger.info("Le conteneur {} a été redémarré avec succès.", containerName);
                                } else {
                                    logger.error("Erreur lors du redémarrage du conteneur {}. Code de sortie: {}. Détails: {}",
                                            containerName, exitCode, errorOutput.toString());
                                }
                            } catch (IOException | InterruptedException ex) {
                                logger.error("Erreur lors du redémarrage du conteneur {}", containerName, ex);
                            }
                        } else {
                            logger.error("Aucun conteneur correspondant n'a été trouvé pour le worker: {}", workerName);
                        }
                    } else {
                        logger.error("Le nom du worker n'a pas pu être extrait du message: {}", decodedContent);
                    }
                    logger.info("Other type of message read in Queue 999: {}", decodedContent);
                }

            } else {
                logger.warn("Failed to fetch messages from queue 999. Status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error while checking messages in Queue 999", e);
        }
    }

    private String getContainerNameByWorker(String workerName) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"docker", "ps", "-a", "--format", "{{.Names}}"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String containerName = reader.lines()
                    .filter(line -> line.contains(workerName))
                    .findFirst()
                    .orElse(null);
            process.waitFor(); // attend la fin du processus
            return containerName;
        } catch (IOException | InterruptedException e) {
            logger.error("Erreur lors de la récupération du nom du conteneur pour le worker: {}", workerName, e);
            return null;
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
