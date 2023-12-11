package com.choi.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class YoloObjectExtra {
	private String classId;
	private Rectangle rect;
	private int depth;
	private String uid;
	private String propertyName;
	private List<String> events;
}
