package com.choi.vo;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UiRect {
	/*
	 * 객체의 중심 좌표
	 */
	public int centerX;
	public int centerY;
	/*
	 * UI 요소 객체의 정보
	 */
	public YoloObjectEntry source;
	/*
	 * UI 요소가 위치하는 그리드 상의 좌표
	 */
	public int gridX;
	public int gridY;
	
	public boolean flagUsed;
	public List<UiRect> children;
	
	public UiObjectInfo toMeta() {
		UiObjectInfo meta = new UiObjectInfo();
		
		meta.setClassId(source.getClassId());
		meta.setProperty(source.getPropertyName());
		meta.setExtraAttribute(source.getExtraAttribute());
		meta.setEvent(source.getEvents());
		meta.setGridX(gridX);
		meta.setGridY(gridY);
		meta.setPosition(source.getPosition());
		meta.setLabelText(source.getLabelText());
		
		if(children != null)
			meta.setColumnCount(children.size());

		meta.setX(source.getRect().getX());
		meta.setY(source.getRect().getY());
		meta.setWidth(source.getRect().getWidth());
		meta.setHeight(source.getRect().getHeight());
		
		return meta;
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("depth=").append(source.getDepth()).append(",");
		str.append("number=").append(source.getNumber()).append(",");
		if(source.getParentNumber() > 0)
			str.append("parentNumber=").append(source.getParentNumber()).append(",");
		if(children != null && children.size() > 0)
			str.append("children=").append(children.stream().map(m -> m.source.getNumber()).collect(Collectors.toList())).append(",");
		str.append("class=").append(source.getClassId()).append(",");
		str.append("centerX=").append(centerX).append(",");
		str.append("centerY=").append(centerY).append(",");
		str.append("gridX=").append(gridX).append(",");
		str.append("gridY=").append(gridY).append("\n");
		
		return str.toString();
	}
	
	private static Comparator<UiRect> gridSort = new Comparator<UiRect>() {
		@Override
		public int compare(UiRect o1, UiRect o2) {
			if(o1.gridY == o2.gridY)
				return o1.gridX >= o2.gridX ? 1 : -1;
			else
				return o1.gridY > o2.gridY ? 1 : -1;
		}
	};
	
	public static Comparator<UiRect> getGridComparator() {
		return gridSort;
	}
}
