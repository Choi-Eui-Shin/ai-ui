package com.choi.core;

import java.util.List;

import com.choi.entity.RuleDetail;
import com.choi.ex.ServiceException;
import com.choi.vo.UiRect;

public interface IBuilder {
	public String generateCode() throws ServiceException;
	
	public void setRule(List<RuleDetail> rules);
	
	public void setElements(List<UiRect> elms);
	
	public String preCode();
	
	public String postCode();
}
