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
public class RuleMaster {
	/*
	 * UUID
	 */
	@Id
	private String uuid;
	/*
	 * 사용자 ID
	 */
	private String userId;
	/*
	 * 대표 이름
	 */
	private String ruleTitle;
	/*
	 * 생성일자
	 */
	private String createDate;
}
