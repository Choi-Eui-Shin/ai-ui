package com.choi.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenerateRequest {
	private String targetTemplateName;
	private List<YoloObjectExtra> uiObjects;
}
