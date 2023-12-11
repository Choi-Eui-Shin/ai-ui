package com.choi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseRestController {
	protected final Logger logger = LoggerFactory.getLogger(BaseRestController.class);
	
	protected void error(Throwable e) {
		logger.error("ERROR", e);
	}
}
