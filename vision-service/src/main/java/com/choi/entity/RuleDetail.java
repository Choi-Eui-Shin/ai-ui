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
	 * UI 태그
	 */
	private String uiTag;
	/*
	 * click 이벤트
	 */
	private String clickEventTag;
	/*
	 * change 이벤트
	 */
	private String changeEventTag;
	/*
	 * UI 추가 속성
	 */
	private String extraAttribute;
	/*
	 * 기본값
	 */
	private String defaultValue;
}
