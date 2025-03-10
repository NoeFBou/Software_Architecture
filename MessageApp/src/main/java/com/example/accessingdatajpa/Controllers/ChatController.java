package com.example.accessingdatajpa.Controllers;

import com.example.accessingdatajpa.Models.Entity.Message;
import com.example.accessingdatajpa.Services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private MessageService chatService;

    /**
     * Envoi d’un message.
     * Exemple d’appel avec Postman (méthode POST) :
     * http://localhost:8080/api/messages?content=Bonjour&personId=1&queueId=2&topicIds=1,3
     */
    @PostMapping("/messages")
    public Message sendMessage(@RequestParam String content,
                               @RequestParam Integer personId,
                               @RequestParam Integer queueId,
                               @RequestParam(required = false) List<Integer> topicIds) {
        return chatService.sendMessage(content, personId, queueId, topicIds);
    }

    /**
     * Récupération des messages d’un topic à partir d’un numéro interne.
     * Exemple : GET http://localhost:8080/api/topics/1/messages?startingNumber=5
     */
    @GetMapping("/topics/{topicId}/messages")
    public List<Message> getMessagesFromTopic(@PathVariable Integer topicId,
                                              @RequestParam Integer startingNumber) {
        return chatService.getMessagesFromTopic(topicId, startingNumber);
    }

    /**
     * Recherche de messages par contenu partiel.
     * Exemple : GET http://localhost:8080/api/messages/search?keyword=bonjour
     */
    @GetMapping("/messages/search")
    public List<Message> searchMessages(@RequestParam String keyword) {
        return chatService.searchMessages(keyword);
    }

    /**
     * Marquer un message comme lu.
     * Exemple : PUT http://localhost:8080/api/messages/10/read
     */
    @PutMapping("/messages/{messageId}/read")
    public Message markMessageAsRead(@PathVariable Integer messageId) {
        return chatService.markMessageAsRead(messageId);
    }

    /**
     * Suppression d’un message d’un topic.
     * Exemple : DELETE http://localhost:8080/api/topics/1/messages/10
     */
    @DeleteMapping("/topics/{topicId}/messages/{messageId}")
    public String deleteMessageFromTopic(@PathVariable Integer topicId, @PathVariable Integer messageId) {
        chatService.deleteMessageFromTopic(topicId, messageId);
        return "Message retiré du topic avec succès.";
    }
}
