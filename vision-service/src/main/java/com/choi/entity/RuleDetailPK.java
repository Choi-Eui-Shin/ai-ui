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
public class RuleDetailPK implements Serializable
{
	/*
	 * UUID
	 */
	@Column(length=36)
	private String uuid;
	/*
	 * UI 객체 클래스 ID
	 */
	@Column(length=80)
	private String clsssId;
}
