package com.choi.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
//import java.util.Optional;
//import com.choi.vo.UiObjectInfo;
import java.util.stream.Collectors;

import com.choi.entity.RuleDetail;
import com.choi.entity.RuleDetailPK;
import com.choi.ex.ServiceException;
import com.choi.vo.GenerateRequest;
import com.choi.vo.UiRect;
import com.choi.vo.YoloObjectEntry;
import com.google.gson.Gson;

public class UiCodeGenerator
{
	/*
	 * 코드 생성 기준 정보
	 */
	private List<RuleDetail> rules;
	/*
	 * UI 추출 정보
	 */
	private List<UiRect> uiRectList;
	/*
	 * 화면 구성 스크립트 생성
	 */
	private IBuilder screenBuilder;
	/*
	 * 자바 스크립트 생성
	 */
	private IBuilder scriptBuilder;
	
	public UiCodeGenerator(List<RuleDetail> rules, List<YoloObjectEntry> objs, IBuilder screen, IBuilder script)
	{
		this.rules = rules;
		this.uiRectList = new ArrayList<>();
		
		for(YoloObjectEntry oe : objs) {
			UiRect ur = new UiRect();
			ur.source = oe;
			ur.centerX = oe.getRect().getCenterX();
			ur.centerY = oe.getRect().getCenterY();
			
			this.uiRectList.add(ur);
		}
		
		this.screenBuilder = screen;
		this.scriptBuilder = script;
	}
	
	/**
	 * @param target
	 */
	private void _buildTree(UiRect target)
	{
		final int number = target.source.getNumber();
		List<UiRect> subList = this.uiRectList.stream().filter(m -> m.flagUsed == false && m.source.getParentNumber() == number).collect(Collectors.toList());
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
			List<UiRect> list = alignX.stream().filter(m -> m.gridY == num).collect(Collectors.toList());
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
		List<UiRect> baseList = this.uiRectList.stream().filter(m -> m.source.getDepth() == 1).collect(Collectors.toList());
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
	
//	/**
//	 * @param list
//	 * @param gen
//	 */
//	private void _generate(List<UiRect> list, IGenerate gen) {
//		list.sort(UiRect.getGridComparator());
//		
//		int cols = 12/list.size();
//		
//		gen.beginRow();
//		
//		for(UiRect ur : list) {
//			UiObjectInfo meta = ur.toMeta();
//			// 시작 태그
//			gen.beginTag(meta, cols);
//			
//			// 하위 요소
//			if(ur.children != null && ur.children.size() > 0) {
//				Optional<UiRect> depth = ur.children.stream().max(new Comparator<UiRect>() {
//					@Override
//					public int compare(UiRect o1, UiRect o2) {
//						return o1.gridY >= o2.gridY ? 1 : -1;
//					}
//				});
//				
//				for(int i = 1; i <= depth.get().gridY; i++) {
//					final int filter = i;
//					List<UiRect> subList = ur.children.stream().filter(m -> m.gridY == filter).toList();
//					_generate(new ArrayList<>(subList), gen);
//				}
//			}
//			
//			// 종료 태그
//			gen.endTag(meta);
//		}
//		
//		gen.endRow();
//	}
//	
//	
//	private void print()
//	{
//		List<UiRect> baseList = this.uiRectList.stream().filter(m -> m.source.getDepth() == 1).toList();
//		IGenerate gen = new IGenerate() {
//			private String START_TAG = "<%s>";
//			private String END_TAG = "</%s>";
//			final private String START_COL_TAG = "<v-col cols='%d'>";
//			final private String END_COL_TAG = "</v-col>";
//			
//			@Override
//			public void endTag(UiObjectInfo meta) {
//				System.out.println(String.format(END_TAG, meta.getClassId()));
//				System.out.println(END_COL_TAG);
//			}
//			
//			@Override
//			public void beginTag(UiObjectInfo meta, int cols) {
//				System.out.println(String.format(START_COL_TAG, cols));
//				System.out.println(String.format(START_TAG, meta.getClassId()));
//			}
//
//			@Override
//			public void beginRow() {
//				System.out.println("<v-row>");
//			}
//
//			@Override
//			public void endRow() {
//				System.out.println("</v-row>");
//			}
//		};
//		
//		_generate(new ArrayList<>(baseList), gen);
//	}
	
	/**
	 * @return
	 */
	public String generateCode() throws ServiceException
	{
		if(this.uiRectList.size() == 0)
			throw new ServiceException("데이터가 없습니다.");
		
		/**
		 * TODO: START - 개발용 코드
		 */
		if(this.rules == null)
			this.rules = _makeRule();
		/**
		 * TODO: END - 개발용 코드
		 */
		
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
		List<UiRect> baseList = this.uiRectList.stream().filter(m -> m.source.getDepth() == 1).collect(Collectors.toList());
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
		StringBuffer code = new StringBuffer();
		
		List<UiRect> uiElements = new ArrayList<>(baseList);
		/*
		 * UI 구성 코드 생성
		 */
		screenBuilder.setRule(rules);
		screenBuilder.setElements(uiElements);
		code.append(screenBuilder.generateCode());
		
		/*
		 * 스크립트 코드 생성
		 */
		scriptBuilder.setElements(uiElements);
		code.append(scriptBuilder.generateCode());
		
		return code.toString();
	}
	
	private List<RuleDetail> _makeRule() {
		List<RuleDetail> rules = new ArrayList<>();
		
		// label
		RuleDetailPK pk = new RuleDetailPK();
		pk.setClsssId("label");
		RuleDetail detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("p");
		detail.setExtraAttribute("class=\"font-weight-regular\"");
		detail.setDefaultValue("LABEL");
		rules.add(detail);
		
		// text
		pk = new RuleDetailPK();
		pk.setClsssId("textbox");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("v-text-field");
		detail.setExtraAttribute("hide-details=\"auto\"");
		rules.add(detail);
		
		// div
		pk = new RuleDetailPK();
		pk.setClsssId("div");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("div");
		rules.add(detail);
		
		// button
		pk = new RuleDetailPK();
		pk.setClsssId("button");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("v-btn");
		detail.setExtraAttribute("color=\"primary\"");
		rules.add(detail);

		// checkbox
		pk = new RuleDetailPK();
		pk.setClsssId("checkbox");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("v-checkbox");
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
		detail.setUiTag("v-col");
		detail.setExtraAttribute("cols=\"${SIZE}\"");
		rules.add(detail);
		
		// container
		pk = new RuleDetailPK();
		pk.setClsssId("_CONTAINER_");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setUiTag("v-container");
		rules.add(detail);
		
		// start
		pk = new RuleDetailPK();
		pk.setClsssId("_ALIGN_START_");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setExtraAttribute("class=\"d-flex justify-start\"");
		rules.add(detail);
		
		// center
		pk = new RuleDetailPK();
		pk.setClsssId("_ALIGN_CENTER_");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setExtraAttribute("class=\"d-flex justify-center\"");
		rules.add(detail);
		
		// end
		pk = new RuleDetailPK();
		pk.setClsssId("_ALIGN_END_");
		detail = new RuleDetail();
		detail.setDetailPk(pk);
		detail.setExtraAttribute("class=\"d-flex justify-end\"");
		rules.add(detail);
		
		return rules;
	}
	
	public static void main(String [] args) {
		try {
			Gson gson = new Gson();
//			GenerateRequest payload = gson.fromJson("{\"targetTemplateName\":\"React (MUI)\",\"uiObjects\":[{\"classId\":\"div\",\"confidence\":0.36449167132377625,\"rect\":{\"x\":0,\"y\":5,\"width\":711,\"height\":603,\"centerY\":306,\"centerX\":355},\"depth\":1,\"uid\":\"ea44f269430b4bf882317b631b50c887\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":17,\"parentNumber\":0,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":0,\"top\":5,\"width\":711,\"height\":603,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8509652018547058,\"rect\":{\"x\":60,\"y\":462,\"width\":536,\"height\":48,\"centerY\":486,\"centerX\":328},\"depth\":2,\"uid\":\"2fc5db884e6d4d5896feaf07028d36a9\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":14,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"14\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":60,\"top\":462,\"width\":536,\"height\":48,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9142618179321289,\"rect\":{\"x\":352,\"y\":157,\"width\":346,\"height\":62,\"centerY\":188,\"centerX\":525},\"depth\":2,\"uid\":\"ceb203be29cb4c5e96a65e14bb45dbc5\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":6,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":352,\"top\":157,\"width\":346,\"height\":62,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9363639950752258,\"rect\":{\"x\":358,\"y\":280,\"width\":340,\"height\":53,\"centerY\":306,\"centerX\":528},\"depth\":2,\"uid\":\"d05fba07ba1e4e9bae6181a538e8460f\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":3,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":358,\"top\":280,\"width\":340,\"height\":53,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.91559237241745,\"rect\":{\"x\":362,\"y\":386,\"width\":336,\"height\":51,\"centerY\":411,\"centerX\":530},\"depth\":2,\"uid\":\"ad818f5515eb4a7e9afe4f28bf7ae812\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":5,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":362,\"top\":386,\"width\":336,\"height\":51,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9339904189109802,\"rect\":{\"x\":20,\"y\":168,\"width\":302,\"height\":56,\"centerY\":196,\"centerX\":171},\"depth\":2,\"uid\":\"44a6bd1255be4407854896beae6b1099\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":4,\"parentNumber\":17,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":20,\"top\":168,\"width\":302,\"height\":56,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9384616017341614,\"rect\":{\"x\":23,\"y\":284,\"width\":300,\"height\":54,\"centerY\":311,\"centerX\":173},\"depth\":2,\"uid\":\"338c1d5daae841eab95a4238d9a403da\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":1,\"parentNumber\":17,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":23,\"top\":284,\"width\":300,\"height\":54,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8388546705245972,\"rect\":{\"x\":26,\"y\":21,\"width\":305,\"height\":52,\"centerY\":47,\"centerX\":178},\"depth\":2,\"uid\":\"8a2f724d8d3c4553b0f39b227e15171f\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":15,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"SIGN UP\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":26,\"top\":21,\"width\":305,\"height\":52,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9035723209381104,\"rect\":{\"x\":33,\"y\":399,\"width\":297,\"height\":47,\"centerY\":422,\"centerX\":181},\"depth\":2,\"uid\":\"36e9e73511ce471db3947d366f34f797\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":8,\"parentNumber\":17,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":33,\"top\":399,\"width\":297,\"height\":47,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"button\",\"confidence\":0.9375773668289185,\"rect\":{\"x\":498,\"y\":508,\"width\":197,\"height\":55,\"centerY\":535,\"centerX\":596},\"depth\":2,\"uid\":\"322e19b3854d4e75a569c14cbfcb7412\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":2,\"parentNumber\":17,\"position\":\"right\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":498,\"top\":508,\"width\":197,\"height\":55,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.9046778082847595,\"rect\":{\"x\":364,\"y\":344,\"width\":232,\"height\":38,\"centerY\":363,\"centerX\":480},\"depth\":2,\"uid\":\"58ae2fadf7c44ce694df1b4e3f8be30c\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":7,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":\"7\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":364,\"top\":344,\"width\":232,\"height\":38,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8628280758857727,\"rect\":{\"x\":356,\"y\":125,\"width\":161,\"height\":36,\"centerY\":143,\"centerX\":436},\"depth\":2,\"uid\":\"fe1d5e607c364d58b009bd44934648f4\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":12,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":\"12\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":356,\"top\":125,\"width\":161,\"height\":36,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.869519829750061,\"rect\":{\"x\":355,\"y\":238,\"width\":118,\"height\":38,\"centerY\":257,\"centerX\":414},\"depth\":2,\"uid\":\"b82a84e35d62461e8a636d60fada74a8\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":11,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":\"11\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":355,\"top\":238,\"width\":118,\"height\":38,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8732373714447021,\"rect\":{\"x\":25,\"y\":351,\"width\":127,\"height\":34,\"centerY\":368,\"centerX\":88},\"depth\":2,\"uid\":\"ce6891ab0b78490f8bb7b4800c50721f\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":10,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"10\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":25,\"top\":351,\"width\":127,\"height\":34,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8833488821983337,\"rect\":{\"x\":18,\"y\":240,\"width\":95,\"height\":34,\"centerY\":257,\"centerX\":65},\"depth\":2,\"uid\":\"33cfab3adf754b4288e6ccbee6f0a65a\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":9,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"9\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":18,\"top\":240,\"width\":95,\"height\":34,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8560853004455566,\"rect\":{\"x\":26,\"y\":121,\"width\":76,\"height\":37,\"centerY\":139,\"centerX\":64},\"depth\":2,\"uid\":\"73025a1a5ba84cce8eacdf7cf30c4cfb\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":13,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"First Name\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":26,\"top\":121,\"width\":76,\"height\":37,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"checkbox\",\"confidence\":0.8166069388389587,\"rect\":{\"x\":51,\"y\":484,\"width\":33,\"height\":29,\"centerY\":498,\"centerX\":67},\"depth\":2,\"uid\":\"aed256a6aa1d435ab8d355685b745502\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":16,\"parentNumber\":17,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":51,\"top\":484,\"width\":33,\"height\":29,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}}]}", GenerateRequest.class);
			GenerateRequest payload = gson.fromJson("{\"targetTemplateName\":\"React (MUI)\",\"uiObjects\":[{\"classId\":\"div\",\"confidence\":0.36449167132377625,\"rect\":{\"x\":0,\"y\":5,\"width\":711,\"height\":603,\"centerY\":306,\"centerX\":355},\"depth\":1,\"uid\":\"ea44f269430b4bf882317b631b50c887\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":17,\"parentNumber\":0,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":0,\"top\":5,\"width\":711,\"height\":603,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8509652018547058,\"rect\":{\"x\":60,\"y\":462,\"width\":536,\"height\":48,\"centerY\":486,\"centerX\":328},\"depth\":2,\"uid\":\"2fc5db884e6d4d5896feaf07028d36a9\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":14,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"14\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":60,\"top\":462,\"width\":536,\"height\":48,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9142618179321289,\"rect\":{\"x\":352,\"y\":157,\"width\":346,\"height\":62,\"centerY\":188,\"centerX\":525},\"depth\":2,\"uid\":\"ceb203be29cb4c5e96a65e14bb45dbc5\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":6,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":352,\"top\":157,\"width\":346,\"height\":62,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9363639950752258,\"rect\":{\"x\":358,\"y\":280,\"width\":340,\"height\":53,\"centerY\":306,\"centerX\":528},\"depth\":2,\"uid\":\"d05fba07ba1e4e9bae6181a538e8460f\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":3,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":358,\"top\":280,\"width\":340,\"height\":53,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.91559237241745,\"rect\":{\"x\":362,\"y\":386,\"width\":336,\"height\":51,\"centerY\":411,\"centerX\":530},\"depth\":2,\"uid\":\"ad818f5515eb4a7e9afe4f28bf7ae812\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":5,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":362,\"top\":386,\"width\":336,\"height\":51,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9339904189109802,\"rect\":{\"x\":20,\"y\":168,\"width\":302,\"height\":56,\"centerY\":196,\"centerX\":171},\"depth\":2,\"uid\":\"44a6bd1255be4407854896beae6b1099\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":4,\"parentNumber\":17,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":20,\"top\":168,\"width\":302,\"height\":56,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9384616017341614,\"rect\":{\"x\":23,\"y\":284,\"width\":300,\"height\":54,\"centerY\":311,\"centerX\":173},\"depth\":2,\"uid\":\"338c1d5daae841eab95a4238d9a403da\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":1,\"parentNumber\":17,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":23,\"top\":284,\"width\":300,\"height\":54,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8388546705245972,\"rect\":{\"x\":26,\"y\":21,\"width\":305,\"height\":52,\"centerY\":47,\"centerX\":178},\"depth\":2,\"uid\":\"8a2f724d8d3c4553b0f39b227e15171f\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":15,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"SIGN UP\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":26,\"top\":21,\"width\":305,\"height\":52,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"textbox\",\"confidence\":0.9035723209381104,\"rect\":{\"x\":33,\"y\":399,\"width\":297,\"height\":47,\"centerY\":422,\"centerX\":181},\"depth\":2,\"uid\":\"36e9e73511ce471db3947d366f34f797\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":8,\"parentNumber\":17,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":33,\"top\":399,\"width\":297,\"height\":47,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"button\",\"confidence\":0.9375773668289185,\"rect\":{\"x\":498,\"y\":508,\"width\":197,\"height\":55,\"centerY\":535,\"centerX\":596},\"depth\":2,\"uid\":\"322e19b3854d4e75a569c14cbfcb7412\",\"propertyName\":\"\",\"events\":[\"click\"],\"extraAttribute\":null,\"number\":2,\"parentNumber\":17,\"position\":\"right\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":498,\"top\":508,\"width\":197,\"height\":55,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.9046778082847595,\"rect\":{\"x\":364,\"y\":344,\"width\":232,\"height\":38,\"centerY\":363,\"centerX\":480},\"depth\":2,\"uid\":\"58ae2fadf7c44ce694df1b4e3f8be30c\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":7,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":\"7\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":364,\"top\":344,\"width\":232,\"height\":38,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8628280758857727,\"rect\":{\"x\":356,\"y\":125,\"width\":161,\"height\":36,\"centerY\":143,\"centerX\":436},\"depth\":2,\"uid\":\"fe1d5e607c364d58b009bd44934648f4\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":12,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":\"Last Name\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":356,\"top\":125,\"width\":161,\"height\":36,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.869519829750061,\"rect\":{\"x\":355,\"y\":238,\"width\":118,\"height\":38,\"centerY\":257,\"centerX\":414},\"depth\":2,\"uid\":\"b82a84e35d62461e8a636d60fada74a8\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":11,\"parentNumber\":17,\"position\":\"middle\",\"labelText\":\"11\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":355,\"top\":238,\"width\":118,\"height\":38,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8732373714447021,\"rect\":{\"x\":25,\"y\":351,\"width\":127,\"height\":34,\"centerY\":368,\"centerX\":88},\"depth\":2,\"uid\":\"ce6891ab0b78490f8bb7b4800c50721f\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":10,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"10\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":25,\"top\":351,\"width\":127,\"height\":34,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8833488821983337,\"rect\":{\"x\":18,\"y\":240,\"width\":95,\"height\":34,\"centerY\":257,\"centerX\":65},\"depth\":2,\"uid\":\"33cfab3adf754b4288e6ccbee6f0a65a\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":9,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"9\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":18,\"top\":240,\"width\":95,\"height\":34,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"label\",\"confidence\":0.8560853004455566,\"rect\":{\"x\":26,\"y\":121,\"width\":76,\"height\":37,\"centerY\":139,\"centerX\":64},\"depth\":2,\"uid\":\"73025a1a5ba84cce8eacdf7cf30c4cfb\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":13,\"parentNumber\":17,\"position\":\"left\",\"labelText\":\"First Name\",\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":26,\"top\":121,\"width\":76,\"height\":37,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}},{\"classId\":\"checkbox\",\"confidence\":0.8166069388389587,\"rect\":{\"x\":51,\"y\":484,\"width\":33,\"height\":29,\"centerY\":498,\"centerX\":67},\"depth\":2,\"uid\":\"aed256a6aa1d435ab8d355685b745502\",\"propertyName\":\"\",\"events\":[],\"extraAttribute\":null,\"number\":16,\"parentNumber\":17,\"position\":\"left\",\"labelText\":null,\"object\":{\"type\":\"rect\",\"version\":\"5.3.0\",\"originX\":\"left\",\"originY\":\"top\",\"left\":51,\"top\":484,\"width\":33,\"height\":29,\"fill\":\"blue\",\"stroke\":null,\"strokeWidth\":1,\"strokeDashArray\":null,\"strokeLineCap\":\"butt\",\"strokeDashOffset\":0,\"strokeLineJoin\":\"miter\",\"strokeUniform\":false,\"strokeMiterLimit\":4,\"scaleX\":1,\"scaleY\":1,\"angle\":0,\"flipX\":false,\"flipY\":false,\"opacity\":0.15,\"shadow\":null,\"visible\":true,\"backgroundColor\":\"\",\"fillRule\":\"nonzero\",\"paintFirst\":\"fill\",\"globalCompositeOperation\":\"source-over\",\"skewX\":0,\"skewY\":0,\"rx\":0,\"ry\":0}}]}", GenerateRequest.class);
			UiCodeGenerator uiGen = new UiCodeGenerator(null,
										payload.getUiObjects(),
										new VuetifyScreenBuilder(),
										new VuetifyScriptBuilder());
			System.out.println(uiGen.generateCode());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
