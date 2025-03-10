package com.example.accessingdatajpa.Models.Repository;

import com.example.accessingdatajpa.Models.Entity.TopicMessage;
import com.example.accessingdatajpa.Models.Entity.TopicMessageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicMessageRepository extends JpaRepository<TopicMessage, TopicMessageId> {

    @Query("SELECT tm FROM TopicMessage tm WHERE tm.topic.id = :topicId AND tm.internalNumber >= :start ORDER BY tm.internalNumber")
    List<TopicMessage> findByTopicAndInternalNumberGreaterThanEqual(@Param("topicId") Integer topicId,
                                                                    @Param("start") Integer start);

    @Query("SELECT tm FROM TopicMessage tm WHERE tm.message.id = :messageId")
    List<TopicMessage> findByMessageId(@Param("messageId") Integer messageId);
}
