package com.example.accessingdatajpa.Controllers;

import com.example.accessingdatajpa.Models.Entity.Message;
import com.example.accessingdatajpa.Services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
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
     * http://localhost:8080/api/messages?content=Bonjour&personId=1&queueId=2&topicIds=1,3
     */
    @PostMapping("/messages")
    public Message sendMessage(@RequestParam String content,
                               @RequestParam Long personId,
                               @RequestParam Long queueId,
                               @RequestParam(required = false) List<Long> topicIds) {
        return messageService.sendMessage(content, personId, queueId, topicIds);
    }

    /**
     * Récupération des messages d’un topic à partir d’un numéro interne.
     * Exemple : GET http://localhost:8080/api/topics/1/messages?startingNumber=5
     */
    @GetMapping("/topics/{topicId}/messages")
    public List<Message> getMessagesFromTopic(@PathVariable Long topicId,
                                              @RequestParam(required = false) Optional<Long> startingNumber) {
        return messageService.getMessagesFromTopic(topicId, startingNumber.orElse(0L));
    }

    /**
     * Lire un message d'une queue donnée en FIFO
     * Exemple : GET http://localhost:8080/api/queues/1/read
     */
    @GetMapping("/queues/{queueId}/read")
    public Message getMessagesFromQueue(@PathVariable Long queueId) {
        return messageService.readAndRemoveFirstMessageFromQueue(queueId);
    }

    /**
     * Recherche de messages par contenu partiel.
     * Exemple : GET http://localhost:8080/api/messages/search?keyword=bonjour
     */
    @GetMapping("/messages/search")
    public List<Message> searchMessages(@RequestParam String keyword) {
        return messageService.searchMessages(keyword);
    }

    /**
     * Suppression d’un message d’un topic.
     * Exemple : DELETE http://localhost:8080/api/topics/1/messages/10
     */
    @DeleteMapping("/topics/{topicId}/messages/{messageId}")
    public String deleteMessageFromTopic(@PathVariable Long topicId, @PathVariable Long messageId) {
        messageService.deleteMessageFromTopic(topicId, messageId);
        return "Message retiré du topic avec succès.";
    }

    /**
     * Marquer un message comme lu.
     * Exemple : PUT http://localhost:8080/api/messages/10/read
     * Pas recommandé d'utiliser cette méthode forcant la lecture d'un message directement,
     * il vaut mieux utiliser les méthodes getMessagesFromQueue et getMessagesFromTopic
     */
    @PutMapping("/messages/{messageId}/read")
    public Message markMessageAsRead(@PathVariable Long messageId) {
        return messageService.markMessageAsRead(messageId, true); // par défaut on lit le message dans la queue mais le systeme d'avoir readFromQueue et readFromTopic est bancal
    }

}
