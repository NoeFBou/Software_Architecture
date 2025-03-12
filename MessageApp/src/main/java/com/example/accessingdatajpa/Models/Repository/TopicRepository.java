package com.example.accessingdatajpa.Models.Repository;

import com.example.accessingdatajpa.Models.Entity.Queue;
import com.example.accessingdatajpa.Models.Entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByName(String name);
}
