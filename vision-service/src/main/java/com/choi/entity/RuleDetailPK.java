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
public class RuleDetailPK implements Serializable
{
	/*
	 * UUID
	 */
	private String uuid;
	/*
	 * UI 객체 클래스 ID
	 */
	private String clsssId;
}
