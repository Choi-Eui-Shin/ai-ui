package com.choi.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameValueParser {
	class NameValueEntry
	{
		public String name;
		public Set<String> values;
		
		public String toString() {
			StringBuffer str = new StringBuffer();
			str.append(name).append("=\"");
			str.append(String.join(" ", values)).append("\"");
			return str.toString();
		}
	}
	
	private Pattern pattern = Pattern.compile("(\\w+)[ ]*=[ ]*\"([^\"]*)\"");
	
	private Map<String, NameValueEntry> attributes;
	
	public NameValueParser() {
		this.attributes = new HashMap<>();
	}
	
	/**
	 * @param str
	 */
	public void addAttribute(String str) {
		if(str == null)
			return;
		
		Matcher matcher = pattern.matcher(str);
		
		while (matcher.find()) {
			String name = matcher.group(1);
			String value = matcher.group(2);
			if(name == null || value == null || name.trim().length() == 0 || value.trim().length() == 0)
				continue;
			
			name = name.trim();
			value = value.trim();
			
			NameValueEntry found = attributes.get(name);
			if(found == null) {
				found = new NameValueEntry();
				found.name = name;
				found.values = new HashSet<>();
				attributes.put(name, found);
			}
			
			String [] vs = value.split(" ");
			for(String s : vs)
				found.values.add(s);
        }
	}
	
	/**
	 *
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		
		for(Iterator<NameValueEntry> it = attributes.values().iterator(); it.hasNext(); ) {
			str.append(it.next().toString()).append(" ");
		}
		
		return str.toString();
	}
	
    public static void main(String[] args) {
    	NameValueParser nvParser = new NameValueParser();
    	nvParser.addAttribute("class=\"d-flex justify-center\" cols= \"6\"");
    	nvParser.addAttribute("class=\"ml-1\"");
    	System.out.println(nvParser);
    }
}
