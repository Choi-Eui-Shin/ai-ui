package com.choi;

public class YoloException extends Exception {
	private String extraData;
	
	public YoloException(Exception e, String extraData) {
		super(e);
		this.extraData = extraData;
	}
	
	public YoloException(Exception e) {
		super(e);
	}
	
	public YoloException(String msg, String extraData) {
		super(msg);
		this.extraData = extraData;
	}
	
	public String getExtraData() {
		return this.extraData;
	}
}
