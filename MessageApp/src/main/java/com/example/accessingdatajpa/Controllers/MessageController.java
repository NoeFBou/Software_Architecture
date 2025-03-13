package com.example.accessingdatajpa.Controllers;

import com.example.accessingdatajpa.Models.Entity.Message;
import com.example.accessingdatajpa.Services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(@RequestParam String content,
                                         @RequestParam Long personId,
                                         @RequestParam(required = false) String queueName,
                                         @RequestParam(required = false) List<String> topicNames) {
        // S'assurer que queueId ou topicIds est fourni
        if (queueName == null && (topicNames == null || topicNames.isEmpty())) {
            return ResponseEntity.badRequest().body("Either a queueName or a list of topicNames must be provided.");
        }

        return messageService.sendMessage(content, personId, queueName, topicNames);
    }


    /**
     * Récupération des messages d’un topic à partir d’un numéro interne.
     * Exemple : GET http://localhost:8080/api/topics/topicName/messages?startingNumber=5
     */
    @GetMapping("/topics/{topicName}/messages")
    public ResponseEntity<?> getMessagesFromTopic(@PathVariable String topicName,
                                              @RequestParam(required = false) Optional<Long> startingNumber) {
        return messageService.getMessagesFromTopic(topicName, startingNumber.orElse(0L));
    }

    /**
     * Lire un message d'une queue donnée en FIFO
     * Exemple : GET http://localhost:8080/api/queues/queue1/read
     */
    @GetMapping("/queues/{queueName}/read")
    public ResponseEntity<?> getMessagesFromQueue(@PathVariable String queueName) {
        return messageService.readAndRemoveFirstMessageFromQueue(queueName);
    }

    /**
     * Recherche de messages par contenu partiel.
     * Exemple : GET http://localhost:8080/api/messages/search?keyword=bonjour
     */
    @GetMapping("/messages/search")
    public ResponseEntity<?> searchMessages(@RequestParam String keyword) {
        return messageService.searchMessages(keyword);
    }

    /**
     * Suppression d’un message d’un topic.
     * Pas Recommandé d'utiliser cette méthode pour supprimer un message d'une queue.
     * Exemple : DELETE http://localhost:8080/api/topics/T1/messages/10
     */
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
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long messageId) {
        return messageService.markMessageAsRead(messageId, true);
        // par défaut, on lit le message dans la queue mais le systeme d'avoir readFromQueue et readFromTopic est bancal
    }

}
