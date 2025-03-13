package com.example.accessingdatajpa.Models.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "TOPIC_MESSAGE")
public class TopicMessage implements Serializable {

    @EmbeddedId
    private TopicMessageId id = new TopicMessageId();

    @ManyToOne
    @MapsId("topicId")
    @JoinColumn(name = "topic_id")
    @JsonBackReference(value = "topic-topicMessage")
    private Topic topic;

    @ManyToOne
    @MapsId("messageId")
    @JoinColumn(name = "msg_id")
    @JsonBackReference(value = "message-topicMessage")
    private Message message;

    @Column(name = "internal_number")
    private Long internalNumber;

    @Column(name = "access_count", nullable = false)
    private int accessCount = 0;

    public TopicMessage() {}

    public TopicMessage(Topic topic, Message message, Long internalNumber) {
        this.topic = topic;
        this.message = message;
        this.internalNumber = internalNumber;
        // Ici, on initialise la clé composite en se basant sur les identifiants
        this.id = new TopicMessageId(topic.getId(), message.getId());
    }

    // Getters et setters...
    public TopicMessageId getId() {
        return id;
    }

    public void setId(TopicMessageId id) {
        this.id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Long getInternalNumber() {
        return internalNumber;
    }

    public void setInternalNumber(Long internalNumber) {
        this.internalNumber = internalNumber;
    }

    public int getAccessCount() {
        return accessCount;
    }
    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }
}
