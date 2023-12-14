package com.choi;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class LabelWriter implements LabelHandler
{
	private String outputFolder;
	private Map<String, Integer> mapper;
	private String lastId = null;
	private String lastCategory = null;
	private StringBuffer content = new StringBuffer();
	
	public LabelWriter(String out, Map<String, Integer> mapper) {
		this.outputFolder = out;
		this.mapper = mapper;
	}
	
	@Override
	public void fetch(String category, String filename, String classId, String left, String top, String right, String bottom)
	{
		if(lastId == null) {
			this.lastId = filename;
			this.lastCategory = category;
		}
		else {
			if(lastId.equals(filename) == false) {
				// 저장된 내용을 파일로 저장한다.
				writeFile();
				
				// 새로운 저장을 시작
				this.lastId = filename;
				this.lastCategory = category;
				this.content = new StringBuffer();
			}
		}
		
		content.append(mapper.get(classId)).append(" ");
		content.append(left).append(" ");
		content.append(top).append(" ");
		content.append(right).append(" ");
		content.append(bottom).append("\n");
	}
	
	/**
	 * 
	 */
	public void finish() {
		if(lastId != null) {
			writeFile();
		}
	}
	
	/**
	 * 
	 */
	private void writeFile() {
		Path path = Paths.get(outputFolder, lastCategory, "labels", lastId + ".txt");
		File targetFile = path.toFile();
		try (FileOutputStream fo = new FileOutputStream(targetFile)) {
			IOUtils.write(content.toString(), fo, "utf-8");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
