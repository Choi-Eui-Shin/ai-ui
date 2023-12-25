package com.choi.core;

import com.choi.ex.ServiceException;

public class ScriptBuilder implements IBuilder
{
	/**
	 *
	 */
	public String generateCode() throws ServiceException
	{
		StringBuffer code = new StringBuffer();
		
		code.append("<script>\n");
		code.append("\n");
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
		code.append("    },\n");
		code.append("};\n");
		code.append("</script>\n");
		code.append("<style>\n");
		code.append("</style>\n");
		
		return code.toString();
	}
}
