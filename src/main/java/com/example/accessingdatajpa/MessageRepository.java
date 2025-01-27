package com.example.accessingdatajpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository  extends CrudRepository<Message, Long> {
    List<Message> findByMessage(String message);
    Message findById(long id);
}
