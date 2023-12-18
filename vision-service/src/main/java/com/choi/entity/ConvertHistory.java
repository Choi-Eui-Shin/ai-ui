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
public class ConvertHistory {
	@EmbeddedId
	private ConvertHistoryPK convertPk;
	/*
	 * 스케치 이미지
	 */
	private byte [] image;
	/*
	 * UI 추출정보 (사용자 입력 정보 포함)
	 */
	private String metaJson;
}
