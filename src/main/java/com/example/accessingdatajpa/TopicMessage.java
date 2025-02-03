package com.example.accessingdatajpa;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

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
    private Integer internalNumber;

    public TopicMessage() {}

    public TopicMessage(Topic topic, Message message, Integer internalNumber) {
        this.topic = topic;
        this.message = message;
        this.internalNumber = internalNumber;
        this.id = new TopicMessageId(topic.getId(), Math.toIntExact(message.getId()));
    }

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

    public Integer getInternalNumber() {
        return internalNumber;
    }

    public void setInternalNumber(Integer internalNumber) {
        this.internalNumber = internalNumber;
    }
}
