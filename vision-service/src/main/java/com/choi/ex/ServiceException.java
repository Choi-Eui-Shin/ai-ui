package com.choi.ex;

public class ServiceException extends Exception {
	public ServiceException(Throwable cause) {
		super(cause);
	}
	
	public ServiceException(String message) {
		super(message);
	}
}
