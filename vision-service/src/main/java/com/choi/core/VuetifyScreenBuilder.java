package com.choi.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.choi.Utils;
import com.choi.entity.RuleDetail;
import com.choi.vo.UiObjectInfo;
import com.choi.vo.UiRect;

public class VuetifyScreenBuilder extends BaseScreenBuilder
{
	public VuetifyScreenBuilder() {
		super();
	}
	
	public VuetifyScreenBuilder(List<RuleDetail> rules, List<UiRect> elms) {
		super(rules, elms);
	}
	
	/**
	 * @param list
	 * @param indent
	 * @return
	 */
	protected String convertToScript(List<UiRect> list, int indent)
	{
		StringBuffer code = new StringBuffer();
		
		list.sort(UiRect.getGridComparator());

		Optional<UiRect> gridY = list.stream().max(new Comparator<UiRect>() {
			@Override
			public int compare(UiRect o1, UiRect o2) {
				return o1.gridY >= o2.gridY ? 1 : -1;
			}
		});

		for(int i = 1; i <= gridY.get().gridY; i++) {
			final int filter = i;
			List<UiRect> subList = list.stream().filter(m -> m.gridY == filter).collect(Collectors.toList());
			
			code.append(Utils.tab2Space(indent+1)).append("<").append(ruleRow.getUiTag()).append(">\n");
			
			int cols = 12/subList.size();
			
			envs.clear();
			
			for(UiRect ur : subList) {
				envs.put("SIZE", String.valueOf(cols));
				
				// column
				code.append(Utils.tab2Space(indent+2))
					.append("<").append(ruleCol.getUiTag());
				
				NameValueParser nvParser = new NameValueParser();
				
				if("middle".equals(ur.source.getPosition()) && cols == 12) {
					if(ruleAlignCenter != null) {
						nvParser.addAttribute(ruleAlignCenter.getExtraAttribute());
					}
				}
				else if("right".equals(ur.source.getPosition()) && cols == 12) {
					if(ruleAlignEnd != null) {
						nvParser.addAttribute(ruleAlignEnd.getExtraAttribute());
					}
				}
				
				if(ruleCol.getExtraAttribute() != null) {
					nvParser.addAttribute(changeEnvs(ruleCol.getExtraAttribute()));
				}
				
				String attrs = nvParser.toString();
				if(attrs.length() > 0) {
					code.append(" ").append(attrs)
					.append(">\n");
				}
				else {
					code.append(">\n");
				}
				
				/*
				 * UI 요소애 대한 처리
				 */
				UiObjectInfo meta = ur.toMeta();
				RuleDetail detail = classMap.get(meta.getClassId());
				if(detail != null) {
					// 시작 태그
					code.append(Utils.tab2Space(indent+3))
						.append("<").append(detail.getUiTag());
					
					// 이벤트 처리
					if(meta.getEvent() != null) {
						for(String str : meta.getEvent()) {
							code.append(" ").append(detail.getClickEventTag()).append("=\"")
								.append(Utils.makeFuctionName(str, meta.getProperty()))
								.append("\"");
						}
					}
					
					if(detail.getExtraAttribute() != null && detail.getExtraAttribute().length() > 0) {
						code.append(" ").append(changeEnvs(detail.getExtraAttribute()))
							.append(">\n");
					}
					else {
						code.append(">\n");
					}
					
					if(meta.getLabelText() != null && meta.getLabelText().length() > 0) {
						code.append(Utils.tab2Space(indent+4)+meta.getLabelText()).append("\n");
					}
					else if(detail.getDefaultValue() != null && detail.getDefaultValue().length() > 0) {
						code.append(Utils.tab2Space(indent+4)+detail.getDefaultValue()).append("\n");
					}
					
					// 하위 요소
					if(ur.children != null && ur.children.size() > 0) {
						code.append(convertToScript(new ArrayList<>(ur.children), indent+3));
					}
					
					code.append(Utils.tab2Space(indent+3))
						.append("</").append(detail.getUiTag()).append(">\n");
				}

				code.append(Utils.tab2Space(indent+2)).append("</").append(ruleCol.getUiTag()).append(">\n");
			}
			
			code.append(Utils.tab2Space(indent+1)).append("</").append(ruleRow.getUiTag()).append(">\n");
		}
		
		return code.toString();
	}
	
	@Override
	public String preCode() {
		return "<template>\n";
	}

	@Override
	public String postCode() {
		return "</template>\n\n";
	}
}
