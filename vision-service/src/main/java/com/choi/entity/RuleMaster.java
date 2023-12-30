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
public class RuleMaster {
	/*
	 * UUID
	 */
	@Id
	@Column(length=36)
	private String uuid;
	/*
	 * 사용자 ID
	 */
	@Column(length=40)
	private String userId;
	/*
	 * 대표 이름
	 */
	@Column(length=1024)
	private String ruleTitle;
	/*
	 * 생성일자
	 */
	@Column(length=14)
	private String createDate;
}
