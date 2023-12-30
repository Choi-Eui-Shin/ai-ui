package com.choi.entity;

import javax.persistence.Column;
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
	@Column(length=40)
	private String userId;
	@Column(length=80)
	private String userPassword;
	@Column(length=80)
	private String userName;
	@Column(length=20)
	private String userRole;
}