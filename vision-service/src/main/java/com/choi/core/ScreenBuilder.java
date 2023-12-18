package com.choi.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.choi.Utils;
import com.choi.entity.RuleDetail;
import com.choi.vo.UiObjectInfo;
import com.choi.vo.UiRect;

public class ScreenBuilder {
	private Map<String, RuleDetail> classMap = null;
	private List<UiRect> elements;
	
	public ScreenBuilder(List<RuleDetail> rules, List<UiRect> list) {
		this.elements = list;
		this.classMap = new HashMap<>();
		
		for(RuleDetail md : rules) {
			this.classMap.put(md.getDetailPk().getClsssId(), md);
		}
	}
	
	/**
	 * @return
	 */
	public String generateScreen() {
		StringBuffer screen = new StringBuffer();
		
		_generate(this.elements, screen, 2, true);
		
		return screen.toString();
	}
	
	/**
	 * @param list
	 * @param screen
	 * @param intent
	 */
	private void _generate(List<UiRect> list, StringBuffer screen, int intent, boolean hasContainer) {
		list.sort(UiRect.getGridComparator());
		
		int cols = 12/list.size();
		
		if(hasContainer) {
			RuleDetail info = classMap.get("_CONTAINER_");
			screen.append(Utils.tab2Space(intent-1)).append("<").append(info.getUiTag()).append(">\n");
		}
		
		RuleDetail row = classMap.get("_ROW_");
		screen.append(Utils.tab2Space(intent)).append("<").append(row.getUiTag()).append(">\n");
//		gen.beginRow();
		
		for(UiRect ur : list) {
			UiObjectInfo meta = ur.toMeta();
			
			/*
			 * 클래스에 정의된 정보를 찾는다.
			 */
			RuleDetail detail = classMap.get(meta.getClassId());
			if(detail == null)
				continue;
			
			// 시작 태그
			screen.append(Utils.tab2Space(intent+1)).append("<").append(detail.getUiTag());
			// click
			if(detail.getClickEventTag() != null) {
			}
			// change
			if(detail.getChangeEventTag() != null) {
			}
			// 추가 속성
			if(detail.getExtraAttribute() != null) {
			}
			screen.append(">\n");
			
			// 하위 요소
			if(ur.children != null && ur.children.size() > 0) {
				Optional<UiRect> depth = ur.children.stream().max(new Comparator<UiRect>() {
					@Override
					public int compare(UiRect o1, UiRect o2) {
						return o1.gridY >= o2.gridY ? 1 : -1;
					}
				});
				
				for(int i = 1; i <= depth.get().gridY; i++) {
					// col
//					RuleDetail col = classMap.get("_COL_");
//					screen.append(Utils.tab2Space(intent+2)).append("<").append(col.getUiTag()).append(">\n");
					
					final int filter = i;
					List<UiRect> subList = ur.children.stream().filter(m -> m.gridY == filter).toList();
					_generate(new ArrayList<>(subList), screen, intent+2, false);
					
					// col
//					screen.append(Utils.tab2Space(intent+2)).append("<").append(col.getUiTag()).append(">\n");
				}
			}
			
			// 종료 태그
			screen.append(Utils.tab2Space(intent+1)).append("</").append(detail.getUiTag()).append(">\n");
		}
		
		screen.append(Utils.tab2Space(intent)).append("</").append(row.getUiTag()).append(">\n");
//		gen.endRow();
		
		if(hasContainer) {
			RuleDetail info = classMap.get("_CONTAINER_");
			screen.append(Utils.tab2Space(intent-1)).append("</").append(info.getUiTag()).append(">\n");
		}
	}
}
