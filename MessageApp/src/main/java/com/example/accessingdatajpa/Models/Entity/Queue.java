package com.example.accessingdatajpa.Models.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private Long id;

    @Column(name = "queue_name", nullable = false, unique = true)
    private String name;

    @Column(name = "queue_description", nullable = false)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "queue")
    @JsonManagedReference(value = "queue_messages")
    private List<Message> messages = new ArrayList<>();

    @Column(name = "access_count", nullable = false)
    private int accessCount = 0;

    // Default constructor for JPA
    public Queue() {}

    // Constructor with all fields (name and description)
    public Queue(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Constructor with name only
    public Queue(String name) {
        this.name = name;
        this.description = null; // Default description
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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