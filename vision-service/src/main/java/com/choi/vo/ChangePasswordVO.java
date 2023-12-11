package com.choi.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangePasswordVO {
	private String oldPassword;
	private String newPassword;
	private String newPasswordRe;
}
