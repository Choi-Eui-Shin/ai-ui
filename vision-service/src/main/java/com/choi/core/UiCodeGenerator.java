package com.choi.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.choi.entity.RuleDetail;
import com.choi.entity.RuleDetailPK;
import com.choi.ex.ServiceException;
import com.choi.target.VuetifyGenerator;
import com.choi.vo.GenerateRequest;
import com.choi.vo.UiObjectInfo;
import com.choi.vo.UiRect;
import com.choi.vo.YoloObjectEntry;
import com.google.gson.Gson;

public class UiCodeGenerator
{
//	class UiRect
//	{
//		/*
//		 * 객체의 중심 좌표
//		 */
//		public int centerX;
//		public int centerY;
//		/*
//		 * UI 요소 객체의 정보
//		 */
//		public YoloObjectEntry source;
//		/*
//		 * UI 요소가 위치하는 그리드 상의 좌표
//		 */
//		public int gridX;
//		public int gridY;
//		
//		public boolean flagUsed;
//		public List<UiRect> children;
//		
//		public UiObjectInfo toMeta() {
//			UiObjectInfo meta = new UiObjectInfo();
//			
//			meta.setClassId(source.getClassId());
//			meta.setProperty(source.getPropertyName());
//			meta.setExtraAttribute(source.getExtraAttribute());
//			meta.setEvent(source.getEvents());
//			meta.setGridX(gridX);
//			meta.setGridY(gridY);
////			private String position;	// LEFT, CENTER, RIGHT
//			
//			if(children != null)
//				meta.setColumnCount(children.size());
//
//			meta.setX(source.getRect().getX());
//			meta.setY(source.getRect().getY());
//			meta.setWidth(source.getRect().getWidth());
//			meta.setHeight(source.getRect().getHeight());
//			
//			return meta;
//		}
//
//		public String toString() {
//			StringBuffer str = new StringBuffer();
//			str.append("depth=").append(source.getDepth()).append(",");
//			str.append("number=").append(source.getNumber()).append(",");
//			if(source.getParentNumber() > 0)
//				str.append("parentNumber=").append(source.getParentNumber()).append(",");
//			if(children != null && children.size() > 0)
//				str.append("children=").append(children.stream().map(m -> m.source.getNumber()).toList()).append(",");
//			str.append("class=").append(source.getClassId()).append(",");
//			str.append("centerX=").append(centerX).append(",");
//			str.append("centerY=").append(centerY).append(",");
//			str.append("gridX=").append(gridX).append(",");
//			str.append("gridY=").append(gridY).append("\n");
//			
//			return str.toString();
//		}
//	}
	
//	private Comparator<UiRect> gridSort = new Comparator<UiRect>() {
//		@Override
//		public int compare(UiRect o1, UiRect o2) {
//			if(o1.gridY == o2.gridY)
//				return o1.gridX >= o2.gridX ? 1 : -1;
//			else
//				return o1.gridY > o2.gridY ? 1 : -1;
//		}
//	};

	/*
	 * 코드 생성 기준이 되는 템플릿 이름
	 */
	private String targetTemplateName;
	
	/*
	 * UI 추출 정보
	 */
	private List<UiRect> uiRectList;
	
	public UiCodeGenerator(String tempName, List<YoloObjectEntry> objs) {
		this.targetTemplateName = tempName;
		this.uiRectList = new ArrayList<>();
		
		for(YoloObjectEntry oe : objs) {
			UiRect ur = new UiRect();
			ur.source = oe;
			ur.centerX = oe.getRect().getCenterX();
			ur.centerY = oe.getRect().getCenterY();
			
			this.uiRectList.add(ur);
		}
	}
	
	/**
	 * @param target
	 */
	private void _buildTree(UiRect target)
	{
		final int number = target.source.getNumber();
		List<UiRect> subList = this.uiRectList.stream().filter(m -> m.flagUsed == false && m.source.getParentNumber() == number).toList();
		if(subList.size() > 0) {
			// 사용 표시
			subList.stream().forEach(m -> m.flagUsed = true);
			
			target.children = subList;
			
			// 하위 객체가 있는지 검사
			for(UiRect sub : subList)
				_buildTree(sub);
		}
	}
	
	/**
	 * @param baseList
	 * @param tolerance
	 */
	private void _assignGridXY(List<UiRect> baseList, int tolerance)
	{
		List<UiRect> alignX = new ArrayList<>();
		List<UiRect> alignY = new ArrayList<>();
		
		alignX.addAll(baseList);
		alignY.addAll(baseList);
		
		/*
		 * X 좌표로 정렬
		 */
		alignX.sort(new Comparator<UiRect>() {
			@Override
			public int compare(UiRect o1, UiRect o2) {
				return o1.centerX == o2.centerX ? 0 : o1.centerX > o2.centerX ? 1 : -1;
			}
		});
		
		/*
		 * Y 좌표로 정렬
		 */
		alignY.sort(new Comparator<UiRect>() {
			@Override
			public int compare(UiRect o1, UiRect o2) {
				return o1.centerY == o2.centerY ? 0 : o1.centerY > o2.centerY ? 1 : -1;
			}
		});

		/*
		 * 그리드 상의 Y 좌표 결정
		 */
		alignY.get(0).gridY = 1;
		for(int i = 1; i < alignY.size(); i++) {
			UiRect ur = alignY.get(i);
			UiRect pre = alignY.get(i-1);
			
			if( pre.centerY-tolerance <= ur.centerY && ur.centerY <= pre.centerY+tolerance ) {
				ur.gridY = pre.gridY;
			}
			else {
				ur.gridY = pre.gridY+1;
			}
		}
		
		/*
		 * 그리드 상의 각 Y 좌표별로 X 좌표 결정
		 */
		int yCount = alignY.stream().max(new Comparator<UiRect>() {
			@Override
			public int compare(UiRect o1, UiRect o2) {
				return o1.gridY > o2.gridY ? 1 : -1;
			}
		}).get().gridY;

		for(int i = 1; i <= yCount; i++) {
			final int num = i;
			List<UiRect> list = alignX.stream().filter(m -> m.gridY == num).toList();
			for(int x = 0; x < list.size(); x++)
				list.get(x).gridX = x+1;
		}
		
		/*
		 * 하위 객체에 대해서도 동일한 규칙을 적용한다
		 */
		for(UiRect ur : baseList) {
			if(ur.children != null && ur.children.size() > 0)
				_assignGridXY(ur.children, tolerance);
		}
	}
	
	/**
	 * 코드 생성을 위해 객체를 가상의 그리드(grid)에 매핑한다.
	 */
	private void _prepare()
	{
		// 1레벨 목록 생성
		List<UiRect> baseList = this.uiRectList.stream().filter(m -> m.source.getDepth() == 1).toList();
		// 사용 표시
		baseList.stream().forEach(m -> m.flagUsed = true);
		// 하위 요소에 대해 동일한 처리
		for(UiRect ur : baseList) {
			_buildTree(ur);
		}
		
		/*
		 * 추출된 객체에서 가장 작은 높이를 찾는다.
		 */
		int minHeight = Integer.MAX_VALUE;
		for(UiRect ur : this.uiRectList) {
			if(ur.source.getRect().getHeight() < minHeight) {
				minHeight = ur.source.getRect().getHeight();
			}
		}
		
		int tolerance = (int)(minHeight * 0.5);
		
		/*
		 * 객체를 배치하기 위한 grid X, Y를 계산한다.
		 */
		_assignGridXY(baseList, tolerance);
	}
	
	/**
	 * @param list
	 * @param gen
	 */
	private void _generate(List<UiRect> list, IGenerate gen) {
		list.sort(UiRect.getGridComparator());
		
		int cols = 12/list.size();
		
		gen.beginRow();
		
		for(UiRect ur : list) {
			UiObjectInfo meta = ur.toMeta();
			// 시작 태그
			gen.beginTag(meta, cols);
			
			// 하위 요소
			if(ur.children != null && ur.children.size() > 0) {
				Optional<UiRect> depth = ur.children.stream().max(new Comparator<UiRect>() {
					@Override
					public int compare(UiRect o1, UiRect o2) {
						return o1.gridY >= o2.gridY ? 1 : -1;
					}
				});
				
				for(int i = 1; i <= depth.get().gridY; i++) {
					final int filter = i;
					List<UiRect> subList = ur.children.stream().filter(m -> m.gridY == filter).toList();
					_generate(new ArrayList<>(subList), gen);
				}
			}
			
			// 종료 태그
			gen.endTag(meta);
		}
		
		gen.endRow();
	}
	
	
	private void print()
	{
		List<UiRect> baseList = this.uiRectList.stream().filter(m -> m.source.getDepth() == 1).toList();
		IGenerate gen = new IGenerate() {
			private String START_TAG = "<%s>";
			private String END_TAG = "</%s>";
			final private String START_COL_TAG = "<v-col cols='%d'>";
			final private String END_COL_TAG = "</v-col>";
			
			@Override
			public void endTag(UiObjectInfo meta) {
				System.out.println(String.format(END_TAG, meta.getClassId()));
				System.out.println(END_COL_TAG);
			}
			
			@Override
			public void beginTag(UiObjectInfo meta, int cols) {
				System.out.println(String.format(START_COL_TAG, cols));
				System.out.println(String.format(START_TAG, meta.getClassId()));
			}

			@Override
			public void beginRow() {
				System.out.println("<v-row>");
			}

			@Override
			public void endRow() {
				System.out.println("</v-row>");
			}
		};
		
		_generate(new ArrayList<>(baseList), gen);
	}
	
	/**
	 * @return
	 */
	public String generateCode() throws ServiceException
	{
		if(this.uiRectList.size() == 0)
			throw new ServiceException("데이터가 없습니다.");
		
		/*
		 * 배치 그리드 생성
		 */
		_prepare();
		
		/*
		 * 화면과 동일하게 배치
		 */
//		this.uiRectList.sort(new Comparator<UiRect>() {
//			@Override
//			public int compare(UiRect o1, UiRect o2) {
//				if(o1.gridY == o2.gridY)
//					return o1.gridX == o2.gridX ? 0 : o1.gridX > o2.gridX ? 1 : -1; 
//				else
//					return o1.gridY > o2.gridY ? 1 : -1;
//			}
//		});
		
		// 중심좌표로 정렬
//		this.uiRectList.sort(new Comparator<UiRect>() {
//			@Override
//			public int compare(UiRect o1, UiRect o2) {
//				if(o1.centerY == o2.centerY)
//					return o1.centerX == o2.centerX ? 0 : o1.centerX > o2.centerX ? 1 : -1; 
//				else
//					return o1.centerY > o2.centerY ? 1 : -1;
//			}
//		});
		
		// left, top 좌표로 정렬
//		this.uiRectList.sort(new Comparator<UiRect>() {
//			@Override
//			public int compare(UiRect o1, UiRect o2) {
//				if(o1.gridY == o2.gridY)
//					return o1.source.getRect().getX() == o2.source.getRect().getX() ? 0 : o1.source.getRect().getX() > o2.source.getRect().getX() ? 1 : -1; 
//				else
//					return o1.source.getRect().getY() > o2.source.getRect().getY() ? 1 : -1;
//			}
//		});
//		System.err.println(this.uiRectList);
		
		/*
		 * DEV
		 */
//		print();
		
		
		/*
		 * 코드 생성
		 */
		List<UiRect> baseList = this.uiRectList.stream().filter(m -> m.source.getDepth() == 1).toList();
		/*
		 * Ver 1 
		 *
		VuetifyGenerator gen = new VuetifyGenerator();
		_generate(new ArrayList<>(baseList), gen);
		
		VelocityGenerator vg = new VelocityGenerator();
		vg.put("StringUtils", new StringUtils());
		vg.put("generateCode", gen.getSourceCode());
		vg.put("componentName", "ChangeIt");
		
		return vg.generate("vuetify.vm");
		*/
		
		/*
		 * Ver 2
		 */
		ScreenBuilder sbuilder = new ScreenBuilder(_makeRule(), new ArrayList<>(baseList));
		return sbuilder.generateScreen();
	}
	
	private List<RuleDetail> _makeRule() {
		List<RuleDetail> rules = new ArrayList<>();
		
		// label
		RuleDetailPK pk = new RuleDetailPK();
		pk.setClsssId("label");
		RuleDetail detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("LABEL");
		rules.add(detail);
		
		// text
		pk = new RuleDetailPK();
		pk.setClsssId("text");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("TEXT");
		rules.add(detail);
		
		// div
		pk = new RuleDetailPK();
		pk.setClsssId("div");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("DIV");
		rules.add(detail);
		
		// button
		pk = new RuleDetailPK();
		pk.setClsssId("button");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("BUTTON");
		rules.add(detail);

		// row
		pk = new RuleDetailPK();
		pk.setClsssId("_ROW_");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("v-row");
		rules.add(detail);
		
		// column
		pk = new RuleDetailPK();
		pk.setClsssId("_COL_");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("v-cols");
		detail.setExtraAttribute("cols=\"3\"");
		rules.add(detail);
		
		// container
		pk = new RuleDetailPK();
		pk.setClsssId("_CONTAINER_");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("v-container");
		rules.add(detail);
		
		return rules;
	}
	
	public static void main(String [] args) {
		try {
			Gson gson = new Gson();
			GenerateRequest payload = gson.fromJson("{\"targetTemplateName\":\"CHOI\",\"uiObjects\":[{\"classId\":\"div\",\"confidence\":0.36449167132377625,\"rect\":{\"x\":0,\"y\":5,\"width\":711,\"height\":603,\"centerY\":306,\"centerX\":355},\"depth\":1,\"uid\":\"c31074de96144f8da7a6926a6307c27a\",\"propertyName\":\"\",\"events\":[],\"number\":17,\"parentNumber\":0},{\"classId\":\"label\",\"confidence\":0.8509652018547058,\"rect\":{\"x\":60,\"y\":462,\"width\":536,\"height\":48,\"centerY\":486,\"centerX\":328},\"depth\":2,\"uid\":\"e3be0b2d761b48a1a7df8dd73daf687a\",\"propertyName\":\"\",\"events\":[],\"number\":14,\"parentNumber\":17},{\"classId\":\"textbox\",\"confidence\":0.9142618179321289,\"rect\":{\"x\":352,\"y\":157,\"width\":346,\"height\":62,\"centerY\":188,\"centerX\":525},\"depth\":2,\"uid\":\"022e826534a641f5a0c5875024edfa90\",\"propertyName\":\"\",\"events\":[],\"number\":6,\"parentNumber\":17},{\"classId\":\"textbox\",\"confidence\":0.9363639950752258,\"rect\":{\"x\":358,\"y\":280,\"width\":340,\"height\":53,\"centerY\":306,\"centerX\":528},\"depth\":2,\"uid\":\"e69740e0c83243cc8a5eb83125b3693b\",\"propertyName\":\"\",\"events\":[],\"number\":3,\"parentNumber\":17},{\"classId\":\"textbox\",\"confidence\":0.91559237241745,\"rect\":{\"x\":362,\"y\":386,\"width\":336,\"height\":51,\"centerY\":411,\"centerX\":530},\"depth\":2,\"uid\":\"8212d2aab6e446cbbdbb71ae872451f1\",\"propertyName\":\"\",\"events\":[],\"number\":5,\"parentNumber\":17},{\"classId\":\"textbox\",\"confidence\":0.9339904189109802,\"rect\":{\"x\":20,\"y\":168,\"width\":302,\"height\":56,\"centerY\":196,\"centerX\":171},\"depth\":2,\"uid\":\"0e1b385beba949ffb9f414c9abe25fe7\",\"propertyName\":\"\",\"events\":[],\"number\":4,\"parentNumber\":17},{\"classId\":\"textbox\",\"confidence\":0.9384616017341614,\"rect\":{\"x\":23,\"y\":284,\"width\":300,\"height\":54,\"centerY\":311,\"centerX\":173},\"depth\":2,\"uid\":\"51e913efd9304409b633951d97e055ae\",\"propertyName\":\"\",\"events\":[],\"number\":1,\"parentNumber\":17},{\"classId\":\"label\",\"confidence\":0.8388546705245972,\"rect\":{\"x\":26,\"y\":21,\"width\":305,\"height\":52,\"centerY\":47,\"centerX\":178},\"depth\":2,\"uid\":\"aee886c5a57f4ae7b504da782acdcc8b\",\"propertyName\":\"\",\"events\":[],\"number\":15,\"parentNumber\":17},{\"classId\":\"textbox\",\"confidence\":0.9035723209381104,\"rect\":{\"x\":33,\"y\":399,\"width\":297,\"height\":47,\"centerY\":422,\"centerX\":181},\"depth\":2,\"uid\":\"6ff1ae5e52b44592bd2052915d92587f\",\"propertyName\":\"\",\"events\":[],\"number\":8,\"parentNumber\":17},{\"classId\":\"button\",\"confidence\":0.9375773668289185,\"rect\":{\"x\":498,\"y\":508,\"width\":197,\"height\":55,\"centerY\":535,\"centerX\":596},\"depth\":2,\"uid\":\"9bcd0c84e0fc48b6b95c0b7912858697\",\"propertyName\":\"\",\"events\":[],\"number\":2,\"parentNumber\":17},{\"classId\":\"label\",\"confidence\":0.9046778082847595,\"rect\":{\"x\":364,\"y\":344,\"width\":232,\"height\":38,\"centerY\":363,\"centerX\":480},\"depth\":2,\"uid\":\"3ab8834706b345c6b4b98818f0152ddb\",\"propertyName\":\"\",\"events\":[],\"number\":7,\"parentNumber\":17},{\"classId\":\"label\",\"confidence\":0.8628280758857727,\"rect\":{\"x\":356,\"y\":125,\"width\":161,\"height\":36,\"centerY\":143,\"centerX\":436},\"depth\":2,\"uid\":\"f8b2b671f8b84cd7a12c0d66dcb62c03\",\"propertyName\":\"\",\"events\":[],\"number\":12,\"parentNumber\":17},{\"classId\":\"label\",\"confidence\":0.869519829750061,\"rect\":{\"x\":355,\"y\":238,\"width\":118,\"height\":38,\"centerY\":257,\"centerX\":414},\"depth\":2,\"uid\":\"0505271fd4434a39aea4cfc0cfeceddd\",\"propertyName\":\"\",\"events\":[],\"number\":11,\"parentNumber\":17},{\"classId\":\"label\",\"confidence\":0.8732373714447021,\"rect\":{\"x\":25,\"y\":351,\"width\":127,\"height\":34,\"centerY\":368,\"centerX\":88},\"depth\":2,\"uid\":\"a35999e1eaca44d3883c9e5556afb9dc\",\"propertyName\":\"\",\"events\":[],\"number\":10,\"parentNumber\":17},{\"classId\":\"label\",\"confidence\":0.8833488821983337,\"rect\":{\"x\":18,\"y\":240,\"width\":95,\"height\":34,\"centerY\":257,\"centerX\":65},\"depth\":2,\"uid\":\"f0da5d73454f41de9b9b1f0b5817f750\",\"propertyName\":\"\",\"events\":[],\"number\":9,\"parentNumber\":17},{\"classId\":\"label\",\"confidence\":0.8560853004455566,\"rect\":{\"x\":26,\"y\":121,\"width\":76,\"height\":37,\"centerY\":139,\"centerX\":64},\"depth\":2,\"uid\":\"0f47e363e84946109747a9449c349e49\",\"propertyName\":\"\",\"events\":[],\"number\":13,\"parentNumber\":17},{\"classId\":\"checkbox\",\"confidence\":0.8166069388389587,\"rect\":{\"x\":51,\"y\":484,\"width\":33,\"height\":29,\"centerY\":498,\"centerX\":67},\"depth\":2,\"uid\":\"5e9006b5533a4a408ab4f580eda3c4cc\",\"propertyName\":\"\",\"events\":[],\"number\":16,\"parentNumber\":17}]}", GenerateRequest.class);
			UiCodeGenerator uiGen = new UiCodeGenerator(payload.getTargetTemplateName(), payload.getUiObjects());
			System.out.println(uiGen.generateCode());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
