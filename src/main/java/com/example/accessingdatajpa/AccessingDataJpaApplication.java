package com.example.accessingdatajpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class AccessingDataJpaApplication {

	private static final Logger log = LoggerFactory.getLogger(AccessingDataJpaApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(AccessingDataJpaApplication.class);
	}

	@Autowired
	CustomerRepository repository;

	@Autowired
	MessageRepository mRep;


	public void addUser(String firstName, String lastName) {
		repository.save(new User(firstName, lastName));
	}

	public void getAllUsers() {
		repository.findAll().forEach(user -> {
			log.info(user.toString());
		});
	}

	public void addMessage(String message, String user) {
		mRep.save(new Message(message, repository.findByLastName(user).get(0)));
	}

	public void deleteMessage(long id) {
		mRep.deleteById(id);
	}

	public void getAllMessages() {
		mRep.findAll().forEach(message -> {
			log.info(message.toString());
		});
	}
}
