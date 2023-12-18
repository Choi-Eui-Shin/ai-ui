package com.choi.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class Users {
	@Id
	private String userId;
	private String userPassword;
	private String userName;
	private String userRole;
}