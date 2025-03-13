package com.example.accessingdatajpa.Models.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "person_messages")
    private List<Message> messages = new ArrayList<>();

    @Column(name = "access_count", nullable = false)
    private int accessCount = 0;

    public Person() {}

    public Person(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getAccessCount() {
        return accessCount;
    }
    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }
}
