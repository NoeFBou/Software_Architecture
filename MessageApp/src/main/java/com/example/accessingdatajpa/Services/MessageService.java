package com.example.accessingdatajpa.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.accessingdatajpa.Models.Entity.Message;
import com.example.accessingdatajpa.Models.Entity.Person;
import com.example.accessingdatajpa.Models.Entity.Queue;
import com.example.accessingdatajpa.Models.Entity.Topic;
import com.example.accessingdatajpa.Models.Entity.TopicMessage;
import com.example.accessingdatajpa.Models.Entity.TopicMessageId;
import com.example.accessingdatajpa.Models.Repository.MessageRepository;
import com.example.accessingdatajpa.Models.Repository.PersonRepository;
import com.example.accessingdatajpa.Models.Repository.QueueRepository;
import com.example.accessingdatajpa.Models.Repository.TopicMessageRepository;
import com.example.accessingdatajpa.Models.Repository.TopicRepository;

import jakarta.transaction.Transactional;

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
    public Message sendMessage(String content, Long personId, Long queueId, List<Long> topicIds) {
        return sendMessage(content, personId, queueId, topicIds, false);
    }

    // Surcharge avec le flag isLogMessage
    @Transactional
    public Message sendMessage(String content, Long personId, Long queueId, List<Long> topicIds, boolean isLogMessage) {
        
        // Recherche ou création de la Person si elle n'existe pas
        Person person;
//        logger.info("DEBUG T personRepository: {}", personRepository);
        Optional<Person> personOpt = personRepository.findByUsername("User" + personId);
//        logger.info("DEBUG T PersonOpt: {}", personOpt);
        if (personOpt.isPresent()) {
            person = personOpt.get();
//            logger.info("DEBUG T Person {} found", person.getUsername());
        } else {
            person = new Person("User" + personId);
            person = personRepository.save(person);
            logger.info("Created new Person: {}", person.getUsername());
        }

        // Recherche ou création de la Queue
        String queueName = "Queue" + queueId;
//      Optional<Queue> queueOpt = queueRepository.findById(queueId);
        Optional<Queue> queueOpt = queueRepository.findByName(queueName); // systeme D
        Queue queue;
        if (queueOpt.isPresent()) {
            queue = queueOpt.get();
        } else {
            Optional<Queue> queueByName = queueRepository.findByName(queueName);
            if (queueByName.isPresent()) {
                queue = queueByName.get();
            } else {
                queue = new Queue(queueName, "Queue automatiquement créée pour l'id " + queueId);
                queue = queueRepository.save(queue);
                logger.info("Created new Queue: {} of id {}", queue.getName(), queue.getId());
            }
        }

        // Création et persistance du message
        Message message = new Message(content, person, queue);
        message = messageRepository.save(message);
        logger.info("Message created with ID {} at {}", message.getId(), message.getCreatedAt());

        // Association du message aux topics (si fournis)
        if (topicIds != null) {
            for (Long topicId : topicIds) {
//                Optional<Topic> topicOpt = topicRepository.findById(topicId);
                Optional<Topic> topicOpt = topicRepository.findByName("DefaultTopic" + topicId); // SYSTEM D POUR CONTOURNER ID AUTOINCREMENTE A CHANGER
                Topic topic;
                if (topicOpt.isPresent()) {
                    topic = topicOpt.get();
                } else {
                    // Création automatique du Topic s'il n'existe pas
                    topic = new Topic("DefaultTopic" + topicId, "Topic auto-créé pour l'ID " + topicId);
                    topic = topicRepository.save(topic);
                    logger.info("Created new Topic: {} with ID {}", topic.getName(), topic.getId());
                }

                // Détermination du prochain numéro interne dans le topic
                Long nextInternalNumber = topic.getTopicMessages().stream()
                        .mapToLong(tm -> tm.getInternalNumber() != null ? tm.getInternalNumber() : 0L)
                        .max()  // No need to cast to int, since we are working with Long
                        .orElse(0L) + 1;  // Default value is 0L, and we add 1 to get the next number

                // Création de l'association dans la table de jointure
                TopicMessage topicMessage = new TopicMessage(topic, message, nextInternalNumber);
                // NE PAS appeler explicitement topicMessageRepository.save(topicMessage);
                // Ajoutez l'association aux collections des entités parente
                topic.getTopicMessages().add(topicMessage);
                message.getTopicMessages().add(topicMessage);
                logger.info("Associated Message {} with Topic {} (internal number {})",
                        message.getId(), topic.getId(), nextInternalNumber);
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
    public List<Message> getMessagesFromTopic(Long topicId, Long startingNumber) {
        // find topic by name of name DefaultTopic + topicId
        Optional<Topic> topicOpt = topicRepository.findByName("DefaultTopic" + topicId); // SYSTEME D A CHANGER

        ////// SYSTEME D A CHANGER
        if (topicOpt.isEmpty()) {
            logger.warn("Topic {} not found", topicId);
            return new ArrayList<>(
                    List.of(new Message("Topic not found", null, null))
            );
        }
        /// /////////

        Topic topic = topicOpt.get();
        topic.setAccessCount(topic.getAccessCount() + 1);
        topicRepository.save(topic);


        Long id = topicOpt.get().getId();

        List<TopicMessage> topicMessages = topicMessageRepository.findByTopicAndInternalNumberGreaterThanEqual(id, startingNumber);
        List<Message> messages = topicMessages.stream()
                .map(TopicMessage::getMessage)
                .collect(Collectors.toList());

        for (Message message : messages) {
//            if (message.getFirstAccessedAt() == null) {
//                message.setFirstAccessedAt(LocalDateTime.now());
//                logger.info("Message {} accessed for the first time at {}", message.getId(), message.getFirstAccessedAt());
//            }
//            message.setReadCount(message.getReadCount() + 1);
//            logger.info("Message {} read count updated to {}", message.getId(), message.getReadCount());

            // on peut juste appeler markMessageAsRead(message.getId(), false) maintenant en spécifiant
            // que le message est lu depuis un topic
            markMessageAsRead(message.getId(), false);

        }

        return messages;
    }

    /**
     * Recherche de messages dont le contenu contient un mot-clé partiel.
     */
    @Transactional
    public List<Message> searchMessages(String keyword) {
        List<Message> messages = messageRepository.findByContentContaining(keyword);

        // Mise à jour des statistiques pour chaque message trouvé
        for (Message message : messages) {
            message.setAccessCount(message.getAccessCount() + 1);
            if (message.getFirstAccessedAt() == null) {
                message.setFirstAccessedAt(LocalDateTime.now());
            }
            // Vous pouvez enregistrer chaque message si vous souhaitez forcer la persistance immédiate,
            // ou laisser la transaction le faire automatiquement.
            messageRepository.save(message);
        }

        logger.info("Search for keyword '{}' returned {} messages", keyword, messages.size());
        return messages;
    }

    /**
     * Marque un message comme lu.
     * Si le message est associé à une queue, il est retiré de la queue et la relation est supprimée
     * (une Queue empile les messages non lus en mode FIFO).
     */
    @Transactional
    public Message markMessageAsRead(Long messageId, Boolean readFromQueue) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message non trouvé");
        }
        Message message = messageOpt.get();

        // Mise à jour des statistiques d'accès
        message.setAccessCount(message.getAccessCount() + 1);

        if (readFromQueue) {
            message.setIsReadFromQueue(true);

            // Retirer le message de sa queue s'il en a une
            Queue queue = message.getQueue();
            if (queue != null) {
                logger.info("Message {} has been read, removing from Queue {}", message.getId(), queue.getId());

                // Retirer le message de la queue
                queue.getMessages().remove(message);
                queueRepository.save(queue);

                // Mettre la référence de la queue à null pour éviter les erreurs
                message.setQueue(null);
                messageRepository.save(message);
            }
        } else {
            message.setIsReadFromTopic(true);
            logger.info("Message {} marked as read from Topic", message.getId());
        }

        // Mise à jour du nombre de lectures et de la date du premier accès
        message.setReadCount(message.getReadCount() + 1);
        if (message.getFirstAccessedAt() == null) {
            message.setFirstAccessedAt(LocalDateTime.now());
            logger.info("Message {} marked as read", message.getId());
        }

        messageRepository.save(message);

        // Si le message n'est plus associé à aucun topic, le supprimer
        List<TopicMessage> associations = topicMessageRepository.findByMessageId(messageId);
        if (associations.isEmpty()) {
            messageRepository.delete(message);
            logger.info("Message {} deleted from database as it has been read from Queue and is not associated with any topic", messageId);
        }

        return message;
    }


    /**
     * Suppression d’un message d’un topic.
     * Si le message n’est plus associé à aucun topic, il est supprimé de la base.
     */
    @Transactional
    public void deleteMessageFromTopic(Long topicId, Long messageId) {
        long startTime = System.currentTimeMillis();

        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message non trouvé");
        }
        Message message = messageOpt.get();

        // Mise à jour des statistiques du message
        message.setAccessCount(message.getAccessCount() + 1);
        if (message.getFirstAccessedAt() == null) {
            message.setFirstAccessedAt(LocalDateTime.now());
        }
        messageRepository.save(message);

        // Mise à jour des statistiques du topic associé (si trouvé)
        Optional<Topic> topicOpt = topicRepository.findById(topicId);
        if (topicOpt.isPresent()) {
            Topic topic = topicOpt.get();
            topic.setAccessCount(topic.getAccessCount() + 1);
            topicRepository.save(topic);
        }

        // Vérification de la condition de suppression : le message doit avoir été lu dans la queue ou ne pas y être présent
        if (!message.getIsReadFromQueue() && message.getQueue() != null) {
            throw new RuntimeException("Impossible de supprimer un message présent dans une queue et non lu");
        }

        // Suppression de l'association entre le message et le topic
        TopicMessageId tmId = new TopicMessageId(topicId, messageId);
        Optional<TopicMessage> tmOpt = topicMessageRepository.findById(tmId);
        if (tmOpt.isEmpty()) {
            throw new RuntimeException("Le message n'est pas associé au topic spécifié.");
        }
        topicMessageRepository.delete(tmOpt.get());
        logger.info("Association between Message {} and Topic {} deleted", messageId, topicId);

        // Si le message n'est plus associé à aucun topic, le supprimer de la base
        List<TopicMessage> associations = topicMessageRepository.findByMessageId(messageId);
        if (associations.isEmpty()) {
            messageRepository.delete(message);
            logger.info("Message {} deleted from database as it is no longer associated with any topic", messageId);
        }

        long endTime = System.currentTimeMillis();
        logger.info("Time taken to delete message {}: {} ms", messageId, (endTime - startTime));
    }


    /**
     * Lit et retire le premier message d'une queue.
     *  Une Queue empile les messages non lus en mode FIFO.
     */
    @Transactional
    public Message readAndRemoveFirstMessageFromQueue(Long queueId) {
        logger.warn("DEBUG QueueId: {}, type: {}", queueId, queueId.getClass());
        // On récupère la queue par son nom
        Optional<Queue> queueOpt = queueRepository.findByName("Queue" + queueId);
        if (queueOpt.isEmpty()) {
            logger.warn("Queue {} not found", queueId);
            return new Message("Queue not found", null, null);
        }

        Queue queue = queueOpt.get();
        // Mise à jour des statistiques de la queue
        queue.setAccessCount(queue.getAccessCount() + 1);
        queueRepository.save(queue);

        if (queue.getMessages().isEmpty()) {
            logger.warn("No messages in Queue {}", queue.getId());
            return new Message("Queue is empty", null, queue);
        }

        // Récupérer le premier message (FIFO)
        Message firstMessage = queue.getMessages().get(0);
        logger.info("Reading first message {} from Queue {}", firstMessage.getId(), queue.getId());

        // Vous pouvez aussi mettre à jour les statistiques du message ici (optionnel si déjà fait dans markMessageAsRead)
        firstMessage.setAccessCount(firstMessage.getAccessCount() + 1);
        if (firstMessage.getFirstAccessedAt() == null) {
            firstMessage.setFirstAccessedAt(LocalDateTime.now());
        }
        messageRepository.save(firstMessage);

        // Marquer le message comme lu (dans la queue)
        markMessageAsRead(firstMessage.getId(), true);

        return firstMessage;
    }



}