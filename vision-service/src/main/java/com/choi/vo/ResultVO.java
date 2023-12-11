package com.choi.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 최의신
 *
 * @param <T>
 */
@Getter
@Setter
public class ResultVO<T> {
	/**
	 * 요청 처리결과를 반환한다.
	 */
	private boolean returnCode;
	/**
	 * 실패시에 오류 내역을 반환한다.
	 */
	private String returnMessage;
	/**
	 * 처리 결과를 반환한다.
	 */
	private T result;
}