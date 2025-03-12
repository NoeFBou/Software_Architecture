package com.example.accessingdatajpa.Models.Entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class TopicMessageId {
    private Integer topicId;
    private Long messageId; // Changement ici de Integer à Long

    public TopicMessageId() {}

    public TopicMessageId(Integer topicId, Long messageId) {
        this.topicId = topicId;
        this.messageId = messageId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
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
