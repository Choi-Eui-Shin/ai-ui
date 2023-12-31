package com.choi.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * UI 요소에 대한 인공지능 추출 정보와 사용자가 입력한 추가 정보를 갖는다.
 * 
 * @author 최의신
 *
 */
@Getter
@Setter
public class UiObjectInfo {
	/*
	 * 객체 클래스
	 */
	private String classId;
	/*
	 * 객체에 지정한 프로퍼티
	 */
	private String property;
	/*
	 * 이벤트
	 */
	private List<String> event;
	/*
	 * 그리드 상의 위치
	 */
	private int gridX;
	private int gridY;
	private String position;	// LEFT, CENTER, RIGHT
	/*
	 * 라벨
	 */
	private String labelText;
	/*
	 * 하위 요소의 갯수
	 */
	private int columnCount;
	/*
	 * 최종 태그에 추가될 문자열
	 */
	private String extraAttribute;
	/*
	 * 원본 이미지상의 좌표값
	 */
	private int x;
	private int y;
	private int width;
	private int height;
}
