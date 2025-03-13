package com.example.accessingdatajpa.Controllers;

import com.example.accessingdatajpa.Models.Entity.Message;
import com.example.accessingdatajpa.Models.Entity.Person;
import com.example.accessingdatajpa.Models.Entity.Queue;
import com.example.accessingdatajpa.Models.Entity.Topic;
import com.example.accessingdatajpa.Models.Repository.MessageRepository;
import com.example.accessingdatajpa.Models.Repository.PersonRepository;
import com.example.accessingdatajpa.Models.Repository.QueueRepository;
import com.example.accessingdatajpa.Models.Repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private TopicRepository topicRepository;

    /**
     * Renvoie des statistiques raffinées sur les messages.
     */
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessageStatistics() {
        List<Message> messages = messageRepository.findAll();
        int totalMessages = messages.size();
        double averageAccessCount = messages.stream().mapToInt(Message::getAccessCount).average().orElse(0);
        int maxAccessCount = messages.stream().mapToInt(Message::getAccessCount).max().orElse(0);
        int minAccessCount = messages.stream().mapToInt(Message::getAccessCount).min().orElse(0);

        Map<String, Object> result = new HashMap<>();
        result.put("totalMessages", totalMessages);
        result.put("averageAccessCount", averageAccessCount);
        result.put("maxAccessCount", maxAccessCount);
        result.put("minAccessCount", minAccessCount);

        // Pour chaque message, on construit un détail incluant son contenu, sa queue et les noms de ses topics
        List<Map<String, Object>> details = messages.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("content", m.getContent());
            map.put("accessCount", m.getAccessCount());
            map.put("readCount", m.getReadCount());
            map.put("createdAt", m.getCreatedAt());

            // Infos sur la queue (ID et nom) si présente
            if (m.getQueue() != null) {
                Map<String, Object> queueMap = new HashMap<>();
                queueMap.put("id", m.getQueue().getId());
                queueMap.put("name", m.getQueue().getName());
                map.put("queue", queueMap);
            } else {
                map.put("queue", null);
            }

            // Liste des noms de topics associés via TopicMessage
            List<String> topicNames = m.getTopicMessages().stream()
                    .map(tm -> tm.getTopic().getName())
                    .collect(Collectors.toList());
            map.put("topics", topicNames);

            return map;
        }).collect(Collectors.toList());
        result.put("messages", details);

        return ResponseEntity.ok(result);
    }


    /**
     * Renvoie des statistiques raffinées sur les personnes.
     */
    @GetMapping("/persons")
    public ResponseEntity<Map<String, Object>> getPersonStatistics() {
        List<Person> persons = personRepository.findAll();
        int totalPersons = persons.size();
        double averageAccessCount = persons.stream().mapToInt(Person::getAccessCount).average().orElse(0);
        int maxAccessCount = persons.stream().mapToInt(Person::getAccessCount).max().orElse(0);
        int minAccessCount = persons.stream().mapToInt(Person::getAccessCount).min().orElse(0);

        Map<String, Object> result = new HashMap<>();
        result.put("totalPersons", totalPersons);
        result.put("averageAccessCount", averageAccessCount);
        result.put("maxAccessCount", maxAccessCount);
        result.put("minAccessCount", minAccessCount);
        // Détails pour chaque personne
        List<Map<String, Object>> details = persons.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("username", p.getUsername());
            map.put("accessCount", p.getAccessCount());
            return map;
        }).collect(Collectors.toList());
        result.put("persons", details);

        return ResponseEntity.ok(result);
    }

    /**
     * Renvoie des statistiques raffinées sur les queues.
     */
    @GetMapping("/queues")
    public ResponseEntity<Map<String, Object>> getQueueStatistics() {
        List<Queue> queues = queueRepository.findAll();
        int totalQueues = queues.size();
        double averageAccessCount = queues.stream().mapToInt(Queue::getAccessCount).average().orElse(0);
        int maxAccessCount = queues.stream().mapToInt(Queue::getAccessCount).max().orElse(0);
        int minAccessCount = queues.stream().mapToInt(Queue::getAccessCount).min().orElse(0);

        Map<String, Object> result = new HashMap<>();
        result.put("totalQueues", totalQueues);
        result.put("averageAccessCount", averageAccessCount);
        result.put("maxAccessCount", maxAccessCount);
        result.put("minAccessCount", minAccessCount);
        // Détails pour chaque queue
        List<Map<String, Object>> details = queues.stream().map(q -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId());
            map.put("name", q.getName());
            map.put("accessCount", q.getAccessCount());
            return map;
        }).collect(Collectors.toList());
        result.put("queues", details);

        return ResponseEntity.ok(result);
    }

    /**
     * Renvoie des statistiques raffinées sur les topics.
     */
    @GetMapping("/topics")
    public ResponseEntity<Map<String, Object>> getTopicStatistics() {
        List<Topic> topics = topicRepository.findAll();
        int totalTopics = topics.size();
        double averageAccessCount = topics.stream().mapToInt(Topic::getAccessCount).average().orElse(0);
        int maxAccessCount = topics.stream().mapToInt(Topic::getAccessCount).max().orElse(0);
        int minAccessCount = topics.stream().mapToInt(Topic::getAccessCount).min().orElse(0);

        Map<String, Object> result = new HashMap<>();
        result.put("totalTopics", totalTopics);
        result.put("averageAccessCount", averageAccessCount);
        result.put("maxAccessCount", maxAccessCount);
        result.put("minAccessCount", minAccessCount);
        // Détails pour chaque topic
        List<Map<String, Object>> details = topics.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("name", t.getName());
            map.put("accessCount", t.getAccessCount());
            return map;
        }).collect(Collectors.toList());
        result.put("topics", details);

        return ResponseEntity.ok(result);
    }
}