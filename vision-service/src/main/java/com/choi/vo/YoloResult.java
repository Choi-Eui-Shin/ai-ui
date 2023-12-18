package com.choi.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class YoloResult {
	/*
	 * 분석결과
	 */
	private boolean returnCode;
	/*
	 * UI 요소 추출 정보
	 */
	private List<YoloObjectEntry> result;
	/*
	 * 원본 이미지
	 */
	private String sourceImage;
	/*
	 * 이미지 크기
	 */
	private int imageWidth;
	private int imageHeight;
}
