package com.choi.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.choi.Utils;
import com.choi.entity.RuleDetail;
import com.choi.ex.ServiceException;
import com.choi.vo.UiObjectInfo;
import com.choi.vo.UiRect;

public class ScreenBuilder implements IBuilder
{
	private Map<String, RuleDetail> classMap = null;
	private List<UiRect> elements;
	private RuleDetail ruleContainer;
	private RuleDetail ruleRow;
	private RuleDetail ruleCol;
	private Map<String, String> envs;
	
	public ScreenBuilder(List<RuleDetail> rules, List<UiRect> list) {
		this.elements = list;
		this.classMap = new HashMap<>();
		this.envs = new HashMap<>();
		
		for(RuleDetail md : rules) {
			this.classMap.put(md.getDetailPk().getClsssId(), md);
		}
		
		ruleRow = classMap.get("_ROW_");
		ruleCol = classMap.get("_COL_");
		ruleContainer = classMap.get("_CONTAINER_");
	}
	
	/**
	 *
	 */
	public String generateCode() throws ServiceException
	{
		if(ruleContainer == null || ruleRow == null || ruleCol == null)
			throw new ServiceException("레이아웃 정보가 없습니다.");
		
		StringBuffer screen = new StringBuffer();

		screen.append(Utils.tab2Space(1)).append("<")
			  .append(ruleContainer.getUiTag()).append(">\n");
		
		screen.append(_convert(this.elements, 1));
		
		screen.append(Utils.tab2Space(1)).append("</")
		      .append(ruleContainer.getUiTag())
		      .append(">\n");
		
		return screen.toString();
	}
	
	/**
	 * @param str
	 * @return
	 */
	private String changeEnvs(String str) {
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
	 * @param list
	 * @param intent
	 * @return
	 */
	private String _convert(List<UiRect> list, int intent)
	{
		StringBuffer code = new StringBuffer();
		
		list.sort(UiRect.getGridComparator());

		Optional<UiRect> depth = list.stream().max(new Comparator<UiRect>() {
			@Override
			public int compare(UiRect o1, UiRect o2) {
				return o1.gridY >= o2.gridY ? 1 : -1;
			}
		});

		for(int i = 1; i <= depth.get().gridY; i++) {
			final int filter = i;
			List<UiRect> subList = list.stream().filter(m -> m.gridY == filter).toList();
			
			code.append(Utils.tab2Space(intent+1)).append("<").append(ruleRow.getUiTag()).append(">\n");
			
			int cols = 12/subList.size();
			
			envs.clear();
			
			for(UiRect ur : subList) {
				envs.put("SIZE", String.valueOf(cols));
				
				// column
				code.append(Utils.tab2Space(intent+2))
					.append("<").append(ruleCol.getUiTag());
				
				if(ruleCol.getExtraAttribute() != null && ruleCol.getExtraAttribute().length() > 0) {
					code.append(" ").append(changeEnvs(ruleCol.getExtraAttribute()))
						.append(">\n");
				}
				else {
					code.append(">\n");
				}
				
				UiObjectInfo meta = ur.toMeta();
				code.append(Utils.tab2Space(intent+3))
					.append("<").append(meta.getClassId());
				
				RuleDetail detail = classMap.get(meta.getClassId());
				if(detail != null) {
					if(detail.getExtraAttribute() != null && detail.getExtraAttribute().length() > 0) {
						code.append(" ").append(changeEnvs(detail.getExtraAttribute()))
							.append(">\n");
					}
					else {
						code.append(">\n");
					}
					
					if(detail.getDefaultValue() != null && detail.getDefaultValue().length() > 0) {
						code.append(Utils.tab2Space(intent+4)+detail.getDefaultValue()).append("\n");
					}
				}
				else {
					code.append(">\n");
				}
						
				// 하위 요소
				if(ur.children != null && ur.children.size() > 0) {
					code.append(_convert(new ArrayList<>(ur.children), intent+3));
				}
				
				code.append(Utils.tab2Space(intent+3))
					.append("</").append(meta.getClassId()).append(">\n");

				code.append(Utils.tab2Space(intent+2)).append("</").append(ruleCol.getUiTag()).append(">\n");
			}
			
			code.append(Utils.tab2Space(intent+1)).append("</").append(ruleRow.getUiTag()).append(">\n");
		}
		
		return code.toString();
	}
}
