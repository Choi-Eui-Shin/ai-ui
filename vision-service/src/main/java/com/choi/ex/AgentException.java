package com.choi.ex;

public class AgentException extends Exception {
	public AgentException(Throwable cause) {
		super(cause);
	}
	
	public AgentException(String message) {
		super(message);
	}
}
