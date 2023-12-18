package com.choi.target;

import com.choi.core.IGenerate;
import com.choi.vo.UiObjectInfo;

public class VuetifyGenerator implements IGenerate
{
	final private String START_TAG = "<%s>";
	final private String END_TAG = "</%s>";
	final private String START_COL_TAG = "<v-col cols='%d'>";
	final private String END_COL_TAG = "</v-col>";
	
	private StringBuffer code = new StringBuffer();
	
	@Override
	public void beginRow() {
		code.append("<v-row>").append("\n");
	}

	@Override
	public void endRow() {
		code.append("</v-row>").append("\n");
	}

	@Override
	public void beginTag(UiObjectInfo meta, int cols) {
		code.append(String.format(START_COL_TAG, cols)).append("\n");
		code.append(String.format(START_TAG, meta.getClassId())).append("\n");
	}

	@Override
	public void endTag(UiObjectInfo meta) {
		code.append(String.format(END_TAG, meta.getClassId())).append("\n");
		code.append(END_COL_TAG).append("\n");
	}
	
	/**
	 * @return
	 */
	public String getSourceCode() {
		return code.toString();
	}
}
