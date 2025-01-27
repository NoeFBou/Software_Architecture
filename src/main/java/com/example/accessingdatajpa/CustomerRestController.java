package com.example.accessingdatajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/QueueMessage")
public class CustomerRestController {

    @Autowired
    AccessingDataJpaApplication app;


    //route to add a User
    @RequestMapping("/add")
    public void addUser(String firstName, String lastName, String city) {
        app.addUser(firstName, lastName);
    }

    //route to get all Users
    @RequestMapping("/allUser")
    public void getAllUsers() {
        app.getAllUsers();
    }

    //route to add a Message
    @RequestMapping("/addMessage")
    public void addMessage(String message, String user) {
        app.addMessage(message, user);
    }

    //route to delete a Message
    @RequestMapping("/deleteMessage")
    public void deleteMessage(long id) {
        app.deleteMessage(id);
    }

    //route to get all Messages
    @RequestMapping("/allMessage")
    public void getAllMessages() {
        app.getAllMessages();
    }



}
