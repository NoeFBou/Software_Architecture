package com.example.accessingdatajpa;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
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



    public Message() {}

    public Message(String content, Person person, Queue queue) {
        this.content = content;
        this.person = person;
        this.queue = queue;
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


}
