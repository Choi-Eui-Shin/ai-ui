package com.choi.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.choi.Utils;
import com.choi.entity.RuleDetail;
import com.choi.ex.ServiceException;
import com.choi.vo.UiRect;

public abstract class BaseScreenBuilder extends BaseBuilder
{
	protected Map<String, String> envs;
	
	public BaseScreenBuilder() {
		this.envs = new HashMap<>();
	}
	
	public BaseScreenBuilder(List<RuleDetail> rules, List<UiRect> elms) {
		this();
		
		super.setElements(elms);
		super.setRule(rules);
	}
	
	/**
	 * @param str
	 * @return
	 */
	protected String changeEnvs(String str) {
		int sx = str.indexOf("${");
		if(sx != -1) {
			int ex = str.indexOf("}", sx + 2);
			if(ex != -1) {
				String id = str.substring(sx+2, ex);
				String val = envs.get(id);
				if(val != null) {
					StringBuffer rs = new StringBuffer();
					rs.append(str.substring(0, sx));
					rs.append(val);
					rs.append(str.substring(ex+1));
					
					return rs.toString();
				}
			}
		}
		
		return str;
	}
	
	/**
	 *
	 */
	public String generateCode() throws ServiceException
	{
		if(ruleContainer == null || ruleRow == null || ruleCol == null)
			throw new ServiceException("레이아웃 정보가 없습니다.");
		
		StringBuffer screen = new StringBuffer();

		String pre = preCode();
		String post = postCode();
		
		if(pre != null)
			screen.append(pre);
		
		screen.append(Utils.tab2Space(1)).append("<")
			  .append(ruleContainer.getUiTag()).append(">\n");
		
		screen.append(convertToScript(this.elements, 1));
		
		screen.append(Utils.tab2Space(1)).append("</")
		      .append(ruleContainer.getUiTag())
		      .append(">\n");
		
		if(post != null)
			screen.append(post);

		return screen.toString();
	}
	
	/**
	 * @param list
	 * @param indent
	 * @return
	 */
	abstract protected String convertToScript(List<UiRect> list, int indent);
}
