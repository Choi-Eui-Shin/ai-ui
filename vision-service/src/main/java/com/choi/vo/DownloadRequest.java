package com.choi.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DownloadRequest {
	private String option;
	private String code;
}
