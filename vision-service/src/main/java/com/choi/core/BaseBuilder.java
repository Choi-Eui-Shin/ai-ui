package com.choi.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.choi.entity.RuleDetail;
import com.choi.vo.UiRect;

public abstract class BaseBuilder implements IBuilder
{
	protected static final String CLASS_ROW = "_ROW_";
	protected static final String CLASS_COL = "_COL_";
	protected static final String CLASS_CONTAINER = "_CONTAINER_";
	protected static final String CLASS_ALIGN_START = "_ALIGN_START_";
	protected static final String CLASS_ALIGN_CENTER = "_ALIGN_CENTER_";
	protected static final String CLASS_END = "_ALIGN_END_";

	protected List<UiRect> elements;
	protected Map<String, RuleDetail> classMap = null;
	
	protected RuleDetail ruleContainer;
	protected RuleDetail ruleRow;
	protected RuleDetail ruleCol;
	protected RuleDetail ruleAlignStart;
	protected RuleDetail ruleAlignCenter;
	protected RuleDetail ruleAlignEnd;
	
	public BaseBuilder() {}
	
//	public BaseBuilder(List<RuleDetail> rules, List<UiRect> elms) {
//		this.setElements(elms);
//		this.setRule(rules);
//	}
	
	/**
	 * @param rules
	 */
	public void setRule(List<RuleDetail> rules) {
		this.classMap = new HashMap<>();
		
		for(RuleDetail md : rules) {
			this.classMap.put(md.getDetailPk().getClsssId(), md);
		}
		
		ruleRow = classMap.get(CLASS_ROW);
		ruleCol = classMap.get(CLASS_COL);
		ruleContainer = classMap.get(CLASS_CONTAINER);
		ruleAlignStart = classMap.get(CLASS_ALIGN_START);
		ruleAlignCenter = classMap.get(CLASS_ALIGN_CENTER);
		ruleAlignEnd = classMap.get(CLASS_END);
	}
	
	/**
	 * @param elms
	 */
	public void setElements(List<UiRect> elms) {
		this.elements = elms;
	}
}
