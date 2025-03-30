package com.example.accessingdatajpa.Services;

import com.example.accessingdatajpa.Models.Entity.*;
import com.example.accessingdatajpa.Models.Entity.Queue;
import com.example.accessingdatajpa.Models.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private TopicRepository topicRepository;

    public Map<String, Object> getMessageStatistics() {
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

        List<Map<String, Object>> details = messages.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("content", m.getContent());
            map.put("accessCount", m.getAccessCount());
            map.put("readCount", m.getReadCount());
            map.put("createdAt", m.getCreatedAt());

            if (m.getQueue() != null) {
                map.put("queue", Map.of("name", m.getQueue().getName()));
            } else {
                map.put("queue", null);
            }

            if (m.getPerson() != null) {
                map.put("user", Map.of("id user", m.getPerson().getId()));
            } else {
                map.put("user", null);
            }

            List<String> topicNames = m.getTopicMessages().stream()
                    .map(tm -> tm.getTopic().getName())
                    .collect(Collectors.toList());
            map.put("topics", topicNames);

            return map;
        }).collect(Collectors.toList());
        result.put("messages", details);

        return result;
    }

    public Map<String, Object> getPersonStatistics() {
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

        List<Map<String, Object>> details = persons.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("username", p.getUsername());
            map.put("accessCount", p.getAccessCount());
            return map;
        }).collect(Collectors.toList());

        result.put("persons", details);

        return result;
    }

    public Map<String, Object> getQueueStatistics() {
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

        List<Map<String, Object>> details = queues.stream().map(q -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId());
            map.put("name", q.getName());
            map.put("accessCount", q.getAccessCount());
            return map;
        }).collect(Collectors.toList());
        result.put("queues", details);

        return result;
    }

    public Map<String, Object> getTopicStatistics() {
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

        List<Map<String, Object>> details = topics.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("name", t.getName());
            map.put("accessCount", t.getAccessCount());
            return map;
        }).collect(Collectors.toList());
        result.put("topics", details);

        return result;
    }
}
