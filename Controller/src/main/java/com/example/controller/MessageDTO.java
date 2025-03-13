package com.example.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO {
    private Long id;
    private int accessCount;

    @JsonAlias("isReadFromTopic")
    private boolean readFromTopic;

    private boolean readFromQueue;
    private String content;
    private List<Object> topicMessages;
    private LocalDateTime createdAt;
    private LocalDateTime firstAccessedAt;
    private int readCount;

    // Getters et Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getAccessCount() {
        return accessCount;
    }
    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }
    public boolean isReadFromTopic() {
        return readFromTopic;
    }
    public void setReadFromTopic(boolean readFromTopic) {
        this.readFromTopic = readFromTopic;
    }
    public boolean isReadFromQueue() {
        return readFromQueue;
    }
    public void setReadFromQueue(boolean readFromQueue) {
        this.readFromQueue = readFromQueue;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public List<Object> getTopicMessages() {
        return topicMessages;
    }
    public void setTopicMessages(List<Object> topicMessages) {
        this.topicMessages = topicMessages;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getFirstAccessedAt() {
        return firstAccessedAt;
    }
    public void setFirstAccessedAt(LocalDateTime firstAccessedAt) {
        this.firstAccessedAt = firstAccessedAt;
    }
    public int getReadCount() {
        return readCount;
    }
    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }
}