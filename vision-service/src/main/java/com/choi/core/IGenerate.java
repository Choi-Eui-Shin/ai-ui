package com.choi.core;

import com.choi.vo.UiObjectInfo;

public interface IGenerate {
	/*
	 * 로우 시작 코드 생
	 */
	public void beginRow();
	
	/*
	 * 로우 종료 코드 생성
	 */
	public void endRow();
	
	/*
	 * 시작태그 코드 생성
	 */
	public void beginTag(UiObjectInfo meta, int cols);
	/*
	 * 종료태그 코드 생성
	 */
	public void endTag(UiObjectInfo meta);
}
