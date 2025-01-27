package com.example.accessingdatajpa;

import java.util.Collection;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name = "name")
	private String firstName;

	@Column(name = "surname")
	private String lastName;

	@OneToMany(mappedBy = "user")
	@Column(name = "messages")
	private Collection<Message> messages;


	protected User() {}



	public User(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Collection<Message> getMessages() {
		return messages;
	}


	@Override
	public String toString() {
		return String.format(
				"Customer[id=%d, firstName='%s', lastName='%s']%s",
				this.firstName, this.lastName
				);

	}
}
