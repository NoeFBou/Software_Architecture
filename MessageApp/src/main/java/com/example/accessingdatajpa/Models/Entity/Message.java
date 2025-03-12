package com.example.accessingdatajpa.Models.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_id")
    private Long id;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "message-topicMessage")
    private List<TopicMessage> topicMessages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @JsonBackReference(value = "person-message")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "queue_id", nullable = false)
    @JsonBackReference(value = "queue-message")
    private Queue queue;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "first_accessed_at")
    private LocalDateTime firstAccessedAt;

    @Column(name = "read_count")
    private int readCount = 0;



    public Message() {}

    public Message(String content, Person person, Queue queue) {
        this.content = content;
        this.person = person;
        this.queue = queue;
        this.createdAt = LocalDateTime.now();

    }

    public List<TopicMessage> getTopicMessages() {
        return topicMessages;
    }

    public void setTopicMessages(List<TopicMessage> topicMessages) {
        this.topicMessages = topicMessages;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
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
