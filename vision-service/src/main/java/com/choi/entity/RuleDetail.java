package com.choi.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class RuleDetail {
	@EmbeddedId
	private RuleDetailPK detailPk;
	/*
	 * UI 추가 속성
	 */
	private String extraAttribute;
	/*
	 * UI 코드
	 */
	private String uiCode;
}
