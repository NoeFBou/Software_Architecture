package com.example.accessingdatajpa.Services;

import com.example.accessingdatajpa.Models.Entity.*;
import com.example.accessingdatajpa.Models.Repository.*;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

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

    /**
     * Envoi d’un message :
     * - Crée le message associé à une personne et à une queue.
     * - Pour chaque topic (si fourni), on crée une relation avec numérotation interne.
     */
    @Transactional
    public Message sendMessage(String content, Integer personId, Integer queueId, List<Integer> topicIds) {
        // Recherche de la personne, création si non trouvée
        Person person;
        Optional<Person> personOpt = personRepository.findById(personId);
        if (personOpt.isPresent()) {
            person = personOpt.get();
        } else {
            person = new Person("User" + personId);
            person = personRepository.save(person);
        }

        Optional<Queue> queueOpt = queueRepository.findById(queueId);
        Queue queue;
        if (queueOpt.isPresent()) {
            queue = queueOpt.get();
        } else {
            // Création d'une nouvelle Queue avec un nom et une description par défaut
            queue = new Queue("Queue" + queueId, "Queue automatiquement créée pour l'id " + queueId);
            queue = queueRepository.save(queue);
        }

        // Création et persistance du message
        Message message = new Message(content, person, queue);
        message = messageRepository.save(message); // pour obtenir l’ID généré

        // Association du message aux topics (si des IDs de topics sont fournis)
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
                }
            }
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
            }
            message.setReadCount(message.getReadCount() + 1);
        }

        return messages;
    }

    /**
     * Recherche de messages dont le contenu contient un mot-clé partiel.
     */
    @Transactional
    public List<Message> searchMessages(String keyword) {
        return messageRepository.findByContentContaining(keyword);
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

        List<TopicMessage> associations = topicMessageRepository.findByMessageId(messageId);
        if (associations.isEmpty()) {
            messageRepository.delete(message);
        }

        long endTime = System.currentTimeMillis();
    }
}