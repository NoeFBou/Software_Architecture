package com.example.accessingdatajpa.Models.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TOPIC")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "topic-topicMessage")
    private List<TopicMessage> topicMessages = new ArrayList<>();

    @Column(name = "access_count", nullable = false)
    private int accessCount = 0;

    public Topic() {}

    public Topic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TopicMessage> getTopicMessages() {
        return topicMessages;
    }

    public void setTopicMessages(List<TopicMessage> topicMessages) {
        this.topicMessages = topicMessages;
    }

    public int getAccessCount() {
        return accessCount;
    }
    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }
}