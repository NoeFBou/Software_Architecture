package com.example.accessingdatajpa.Services;

import com.example.accessingdatajpa.Controllers.ChatController;
import com.example.accessingdatajpa.Models.Entity.*;
import com.example.accessingdatajpa.Models.Repository.*;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private static final String LOG_TOPIC_NAME = "log";

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TopicMessageRepository topicMessageRepository;

    @Autowired
    private LogSenderService logSenderService;

    /**
     * Envoi d’un message :
     * - Crée le message associé à une personne et à une queue.
     * - Pour chaque topic (si fourni), on crée une relation avec numérotation interne.
     */
    @Transactional
    public Message sendMessage(String content, Integer personId, Integer queueId, List<Integer> topicIds) {
        return sendMessage(content, personId, queueId, topicIds, false);
    }

    // Surcharge avec le flag isLogMessage
    @Transactional
    public Message sendMessage(String content, Integer personId, Integer queueId, List<Integer> topicIds, boolean isLogMessage) {
        // Recherche ou création de la Person
        Person person;
        Optional<Person> personOpt = personRepository.findById(personId);
        if (personOpt.isPresent()) {
            person = personOpt.get();
        } else {
            person = new Person("User" + personId);
            person = personRepository.save(person);
            logger.info("Created new Person: {}", person.getUsername());
        }

        // Recherche ou création de la Queue
        Optional<Queue> queueOpt = queueRepository.findById(queueId);
        Queue queue;
        if (queueOpt.isPresent()) {
            queue = queueOpt.get();
        } else {
            queue = new Queue("Queue" + queueId, "Queue automatically created for id " + queueId);
            queue = queueRepository.save(queue);
            logger.info("Created new Queue: {}", queue.getName());
        }

        // Création et persistance du message
        Message message = new Message(content, person, queue);
        message = messageRepository.save(message);
        logger.info("Message created with ID {} at {}", message.getId(), message.getCreatedAt());

        // Association du message aux topics (si fournis)
        if (topicIds != null) {
            for (Integer topicId : topicIds) {
                Optional<Topic> topicOpt = topicRepository.findById(topicId);
                if (topicOpt.isPresent()) {
                    Topic topic = topicOpt.get();
                    // Détermination du prochain numéro interne dans le topic
                    int nextInternalNumber = topic.getTopicMessages().stream()
                            .mapToInt(tm -> tm.getInternalNumber() != null ? tm.getInternalNumber() : 0)
                            .max().orElse(0) + 1;
                    TopicMessage topicMessage = new TopicMessage();
                    topicMessage.setTopic(topic);
                    topicMessage.setMessage(message);
                    topicMessage.setInternalNumber(nextInternalNumber);
                    topicMessageRepository.save(topicMessage);
                    // Mise à jour des collections bidirectionnelles
                    topic.getTopicMessages().add(topicMessage);
                    message.getTopicMessages().add(topicMessage);
                    logger.info("Associated Message {} with Topic {} (internal number {})",
                            message.getId(), topic.getId(), nextInternalNumber);
                } else {
                    logger.warn("Topic with ID {} not found; skipping association", topicId);
                }
            }
        }

        // Envoyer le log à l'application externe si ce n'est pas un message de log
        if (!isLogMessage) {
            String logMsg = "Received message with ID " + message.getId() +
                    " from Person " + person.getUsername() +
                    " with content: " + message.getContent();
            logSenderService.sendLog(logMsg);
        }

        return message;
    }



    /**
     * Récupère la liste des messages d’un topic à partir d’un numéro interne donné.
     */
    @Transactional
    public List<Message> getMessagesFromTopic(Integer topicId, Integer startingNumber) {
        List<TopicMessage> topicMessages = topicMessageRepository.findByTopicAndInternalNumberGreaterThanEqual(topicId, startingNumber);
        List<Message> messages = topicMessages.stream()
                .map(TopicMessage::getMessage)
                .collect(Collectors.toList());

        for (Message message : messages) {
            if (message.getFirstAccessedAt() == null) {
                message.setFirstAccessedAt(LocalDateTime.now());
                logger.info("Message {} accessed for the first time at {}", message.getId(), message.getFirstAccessedAt());
            }
            message.setReadCount(message.getReadCount() + 1);
            logger.info("Message {} read count updated to {}", message.getId(), message.getReadCount());
        }

        return messages;
    }

    /**
     * Recherche de messages dont le contenu contient un mot-clé partiel.
     */
    @Transactional
    public List<Message> searchMessages(String keyword) {
        List<Message> messages = messageRepository.findByContentContaining(keyword);
        logger.info("Search for keyword '{}' returned {} messages", keyword, messages.size());
        return messages;
    }

    /**
     * Marque un message comme lu.
     */
    @Transactional
    public Message markMessageAsRead(Integer messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message non trouvé");
        }
        Message message = messageOpt.get();
        message.setIsRead(true);
        message.setReadCount(message.getReadCount() + 1);
        if (message.getFirstAccessedAt() == null) {
            message.setFirstAccessedAt(LocalDateTime.now());
        }
        logger.info("Message {} marked as read", message.getId());
        return messageRepository.save(message);
    }

    /**
     * Suppression d’un message d’un topic.
     * La suppression est autorisée uniquement si le message a été lu dans sa queue.
     * Si le message n’est plus associé à aucun topic, il est supprimé de la base.
     */
    @Transactional
    public void deleteMessageFromTopic(Integer topicId, Integer messageId) {
        long startTime = System.currentTimeMillis();

        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message non trouvé");
        }
        Message message = messageOpt.get();

        if (!Boolean.TRUE.equals(message.getIsRead())) {
            throw new RuntimeException("Impossible de supprimer un message qui n'a pas été lu dans sa queue.");
        }

        TopicMessageId tmId = new TopicMessageId(topicId, messageId);
        Optional<TopicMessage> tmOpt = topicMessageRepository.findById(tmId);
        if (tmOpt.isEmpty()) {
            throw new RuntimeException("Le message n'est pas associé au topic spécifié.");
        }
        topicMessageRepository.delete(tmOpt.get());
        logger.info("Association between Message {} and Topic {} deleted", messageId, topicId);

        List<TopicMessage> associations = topicMessageRepository.findByMessageId(messageId);
        if (associations.isEmpty()) {
            messageRepository.delete(message);
            logger.info("Message {} deleted from database as it is no longer associated with any topic", messageId);
        }

        long endTime = System.currentTimeMillis();
        logger.info("Time taken to delete message {}: {} ms", messageId, (endTime - startTime));
    }

}