package com.example.accessingdatajpa.Models.Repository;

import com.example.accessingdatajpa.Models.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByContentContaining(String keyword);
}
