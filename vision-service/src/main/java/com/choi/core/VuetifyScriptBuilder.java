package com.choi.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.choi.Utils;
import com.choi.entity.RuleDetail;
import com.choi.ex.ServiceException;
import com.choi.vo.UiRect;

public class VuetifyScriptBuilder extends BaseBuilder
{
	public VuetifyScriptBuilder() {
	}
	
	public VuetifyScriptBuilder(List<RuleDetail> rules, List<UiRect> elms) {
		super.setElements(elms);
		super.setRule(rules);
	}
	
	/**
	 * @param elems
	 * @param eventList
	 */
	private void findEvent(List<UiRect> elems, Set<String> eventList) {
		for(UiRect ui : elems) {
			if(ui.source.getEvents() != null) {
				String handlerName = ui.source.getHandlerName();
				for(String evt : ui.source.getEvents())
					eventList.add(Utils.makeFuctionName(evt, handlerName));
				
				if(ui.children != null)
					findEvent(ui.children, eventList);
			}
		}
	}
	
	/**
	 *
	 */
	public String generateCode() throws ServiceException
	{
		StringBuffer code = new StringBuffer();
		
		/*
		 * 이벤트 핸들러 추출
		 */
		Set<String> eventList = new HashSet<>();
		
		if(elements != null) {
			findEvent(elements, eventList);
		}
		
		code.append("export default {\n");
		code.append("    name: \"component\",\n");
		code.append("\n");
		code.append("    components: {\n");
		code.append("    },\n");
		code.append("\n");
		code.append("    data() {\n");
		code.append("        return {\n");
		code.append("        };\n");
		code.append("    },\n");
		code.append("\n");
		code.append("    mounted() {\n");
		code.append("    },\n");
		code.append("\n");
		code.append("    computed: {\n");
		code.append("    },\n");
		code.append("\n");
		code.append("    methods: {\n");
		
		for(String name : eventList) {
			code.append(Utils.tab2Space(2))
			    .append("function ")
			    .append(name)
			    .append("() {\n")
			    .append(Utils.tab2Space(2))
			    .append("}\n\n");
		}
		
		code.append("    },\n");
		code.append("};\n");
		
		return code.toString();
	}

	@Override
	public String preCode() {
		return "<script>\n\n";
	}

	@Override
	public String postCode() {
		StringBuffer code = new StringBuffer();
		
		code.append("</script>\n");
		code.append("<style>\n");
		code.append("</style>\n");
		
		return code.toString();
	}
}
