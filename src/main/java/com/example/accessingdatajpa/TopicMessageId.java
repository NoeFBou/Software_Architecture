package com.example.accessingdatajpa;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class TopicMessageId {
    private Integer topicId;
    private Integer messageId;

    public TopicMessageId() {}

    public TopicMessageId(Integer topicId, Integer messageId) {
        this.topicId = topicId;
        this.messageId = messageId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicMessageId that = (TopicMessageId) o;
        return Objects.equals(topicId, that.topicId) &&
                Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicId, messageId);
    }
}
