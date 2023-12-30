package com.choi.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GenerateRequest {
	/*
	 * 코드 생성 기준이 되는 템플릿 이름
	 */
	private String targetTemplateUuid;
	/*
	 * UI 추출 정보
	 */
	private List<YoloObjectEntry> uiObjects;
}
