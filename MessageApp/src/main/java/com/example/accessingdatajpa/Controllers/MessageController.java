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
@Tag(name = "Messages", description = "Operations endpoints related to messages management in queues and topics")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * Sending a msg
     * Ex :
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
     * Retrieve messages from a topic (starting from a given number if not given 0)
     * Ex : GET http://localhost:8080/api/topics/topicName/messages?startingNumber=5
     */
    @Operation(summary = "Retrieve messages from a topic", description = "Fetch messages from a topic starting from a given number")
    @GetMapping("/topics/{topicName}/messages")
    public ResponseEntity<?> getMessagesFromTopic(@PathVariable String topicName,
                                              @RequestParam(required = false) Optional<Long> startingNumber) {
        return messageService.getMessagesFromTopic(topicName, startingNumber.orElse(0L));
    }

    /**
     *  Read message from queue in FIFO
     * Ex : GET http://localhost:8080/api/queues/queue1/read
     */
    @Operation(summary = "Read message from queue", description = "Reads and removes the first message from a queue")
    @GetMapping("/queues/{queueName}/read")
    public ResponseEntity<?> getMessagesFromQueue(@PathVariable String queueName) {
        return messageService.readAndRemoveFirstMessageFromQueue(queueName);
    }

    /**
     * Search messages by keyword
     * Ex : GET http://localhost:8080/api/messages/search?keyword=bonjour
     */
    @Operation(summary = "Search messages", description = "Search messages by keyword")
    @GetMapping("/messages/search")
    public ResponseEntity<?> searchMessages(@RequestParam String keyword) {
        return messageService.searchMessages(keyword);
    }

    /**
     * Delete message from topic
     * Not recommended to use this method to delete a message from a queue (this forces the message to be deleted from the queue)
     * Ex : DELETE http://localhost:8080/api/topics/T1/messages/10
     */
    @Operation(summary = "Delete message from topic", description = "Deletes a message from a specific topic")
    @DeleteMapping("/topics/{topicName}/messages/{messageId}")
    public ResponseEntity<?> deleteMessageFromTopic(@PathVariable String topicName, @PathVariable Long messageId) {
        messageService.deleteMessageFromTopic(topicName, messageId);
        return ResponseEntity.ok("Message successfully deleted from topic.");
    }

    /**
     * Mark message as read
     * Ex : PUT http://localhost:8080/api/messages/topicName/read
     * Not recommended to use this method to force read a message directly,
     * it is better to use the getMessagesFromQueue and getMessagesFromTopic methods
     */
    @Operation(summary = "Mark message as read", description = "Marks a message as read")
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long messageId) {
        return messageService.markMessageAsRead(messageId, true);
        // par défaut, on lit le message dans la queue mais le systeme d'avoir readFromQueue et readFromTopic est bancal
    }

}
