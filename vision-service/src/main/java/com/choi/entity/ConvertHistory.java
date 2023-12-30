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
public class ConvertHistory {
	@EmbeddedId
	private ConvertHistoryPK convertPk;
	/*
	 * 화면 타이틀
	 */
	@Column(length=128)
	private String screenTitle;
	/*
	 * 스케치 이미지
	 */
	private byte [] image;
	/*
	 * UI 추출정보 (사용자 입력 정보 포함)
	 */
	@Column(length=8192)
	private String metaJson;
}
