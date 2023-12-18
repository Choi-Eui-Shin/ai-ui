package com.choi.entity;

import java.io.Serializable;

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
	private String userId;
	/*
	 * 변환일자
	 */
	private String createDate;
}
