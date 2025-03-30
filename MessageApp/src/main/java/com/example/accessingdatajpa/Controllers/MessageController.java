package com.example.accessingdatajpa.Controllers;

import com.example.accessingdatajpa.Models.Entity.Message;
import com.example.accessingdatajpa.Services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * Envoi d’un message.
     * Exemple d’appel avec Postman (méthode POST) :
     * http://localhost:8080/api/messages?content=Bonjour&personId=1&queueName=Q2&topicNames=1,Q3
     */
    @Operation(summary = "Send a message", description = "Sends a message to a queue or topics")
    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(@RequestParam String content,
                                         @RequestParam Long personId,
                                         @RequestParam(required = false) String queueName,
                                         @RequestParam(required = false) List<String> topicNames) {
        // S'assurer que queueId ou topicIds est fourni
        if (queueName == null && (topicNames == null || topicNames.isEmpty())) {
            return ResponseEntity.badRequest().body("Either a queueName or a list of topicNames must be provided.");
        }

        // Si le message est destiné à être un log, on force isLogMessage à true.
        boolean isLogMessage = topicNames != null && topicNames.contains("log");
        return messageService.sendMessage(content, personId, queueName, topicNames, isLogMessage);
    }


    /**
     * Récupération des messages d’un topic à partir d’un numéro interne.
     * Exemple : GET http://localhost:8080/api/topics/topicName/messages?startingNumber=5
     */
    @Operation(summary = "Retrieve messages from a topic", description = "Fetch messages from a topic starting from a given number")
    @GetMapping("/topics/{topicName}/messages")
    public ResponseEntity<?> getMessagesFromTopic(@PathVariable String topicName,
                                              @RequestParam(required = false) Optional<Long> startingNumber) {
        return messageService.getMessagesFromTopic(topicName, startingNumber.orElse(0L));
    }

    /**
     * Lire un message d'une queue donnée en FIFO
     * Exemple : GET http://localhost:8080/api/queues/queue1/read
     */
    @Operation(summary = "Read message from queue", description = "Reads and removes the first message from a queue")
    @GetMapping("/queues/{queueName}/read")
    public ResponseEntity<?> getMessagesFromQueue(@PathVariable String queueName) {
        return messageService.readAndRemoveFirstMessageFromQueue(queueName);
    }

    /**
     * Recherche de messages par contenu partiel.
     * Exemple : GET http://localhost:8080/api/messages/search?keyword=bonjour
     */
    @Operation(summary = "Search messages", description = "Search messages by keyword")
    @GetMapping("/messages/search")
    public ResponseEntity<?> searchMessages(@RequestParam String keyword) {
        return messageService.searchMessages(keyword);
    }

    /**
     * Suppression d’un message d’un topic.
     * Pas Recommandé d'utiliser cette méthode pour supprimer un message d'une queue.
     * Exemple : DELETE http://localhost:8080/api/topics/T1/messages/10
     */
    @Operation(summary = "Delete message from topic", description = "Deletes a message from a specific topic")
    @DeleteMapping("/topics/{topicName}/messages/{messageId}")
    public ResponseEntity<?> deleteMessageFromTopic(@PathVariable String topicName, @PathVariable Long messageId) {
        messageService.deleteMessageFromTopic(topicName, messageId);
        return ResponseEntity.ok("Message successfully deleted from topic.");
    }

    /**
     * Marquer un message comme lu.
     * Exemple : PUT http://localhost:8080/api/messages/topicName/read
     * Pas recommandé d'utiliser cette méthode forçant la lecture d'un message directement,
     * il vaut mieux utiliser les méthodes getMessagesFromQueue et getMessagesFromTopic
     */
    @Operation(summary = "Mark message as read", description = "Marks a message as read")
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long messageId) {
        return messageService.markMessageAsRead(messageId, true);
        // par défaut, on lit le message dans la queue mais le systeme d'avoir readFromQueue et readFromTopic est bancal
    }

}
