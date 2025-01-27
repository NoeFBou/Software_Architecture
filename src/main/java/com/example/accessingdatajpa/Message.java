package com.example.accessingdatajpa;

import jakarta.persistence.*;

@Entity
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "message")
    private String message;

    @ManyToOne
    private User user;

    public Message() {}

    public Message(String message, User user) {
        this.message = message;
        this.user = user;
    }


    public String getMessage() {
        return message;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
