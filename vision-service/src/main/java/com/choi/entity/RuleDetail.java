package com.choi.entity;

import javax.persistence.Column;
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
	 * UI 태그
	 */
	@Column(length=80)
	private String uiTag;
	/*
	 * click 이벤트
	 */
	@Column(length=1024)
	private String clickEventTag;
	/*
	 * change 이벤트
	 */
	@Column(length=1024)
	private String changeEventTag;
	/*
	 * input 이벤트
	 */
	@Column(length=1024)
	private String inputEventTag;
	/*
	 * UI 추가 속성
	 */
	@Column(length=1024)
	private String extraAttribute;
	/*
	 * 기본값
	 */
	@Column(length=1024)
	private String defaultValue;
}
