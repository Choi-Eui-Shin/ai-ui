package com.choi.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginVO {
	private String userId;
	private String userName;
	private String userPassword;
	private String userRole;
}
