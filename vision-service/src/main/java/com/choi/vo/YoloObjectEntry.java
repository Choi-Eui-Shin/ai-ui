package com.choi.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class YoloObjectEntry {
	private String classId;
	private double confidence;
	private Rectangle rect;
	private int depth;
	private String uid;
	
	public int area() {
		if(rect == null)
			return 0;
		else
			return rect.area();
	}
}
