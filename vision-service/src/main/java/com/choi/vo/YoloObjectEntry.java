package com.choi.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class YoloObjectEntry {
	/*
	 * 클래스 이름
	 */
	private String classId;
	/*
	 * 신뢰도
	 */
	private double confidence;
	/*
	 * 객체 경계 박스
	 */
	private Rectangle rect;
	/*
	 * 객체 계층 구조 (1, 2, 3...)
	 */
	private int depth;
	/*
	 * UUID
	 */
	private String uid;
	/*
	 * 객체에 할당된 프로퍼티 이름
	 */
	private String propertyName;
	/*
	 * 객체에 할당된 이벤트 이름
	 */
	private List<String> events;
	/*
	 * 추가 속성
	 */
	private String extraAttribute;
	/*
	 * 각 객체에 부여된 번호
	 */
	private int number;
	/*
	 * 부모 객체 번호
	 */
	private int parentNumber;
	
	/**
	 * 객체의 넓이를 반환한다.
	 * @return
	 */
	public int area() {
		if(rect == null)
			return 0;
		else
			return rect.area();
	}
}
