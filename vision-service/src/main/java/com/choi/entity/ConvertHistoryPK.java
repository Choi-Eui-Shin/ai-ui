package com.choi.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@Embeddable
public class ConvertHistoryPK implements Serializable
{
	/*
	 * 사용자 ID
	 */
	@Column(length=40)
	private String userId;
	/*
	 * 변환일자
	 */
	@Column(length=14)
	private String createDate;
}
