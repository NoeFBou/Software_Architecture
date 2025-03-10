package com.example.accessingdatajpa.Models.Repository;

import com.example.accessingdatajpa.Models.Entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
}
