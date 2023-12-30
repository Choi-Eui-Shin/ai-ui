package com.choi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Calendar;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

public class Utils {
	
	/**
	 * @param zis
	 * @return
	 */
	public static String readZip(ZipInputStream zis) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		byte [] buffer = new byte[1024];
		try
		{
			int len = 0;
			while((len = zis.read(buffer)) > 0) {
				bo.write(buffer, 0, len);
			}
		}catch(Exception e) {
		}
		
		return bo.toString(Charset.forName("utf-8"));
	}
	
	
	/**
	 * @param cardinality
	 * @param parent
	 * @return
	 */
	public static String toPlantumlRelation(String cardinality, boolean parent, String dir)
	{
		StringBuffer erd = new StringBuffer();
		
		if("ONE_OR_MORE".equals(cardinality))
			erd.append(parent ? "}|" : "|{");
		else if("ZERO_OR_MORE".equals(cardinality))
			erd.append(parent ? "}o" : "o{");
		else if("ZERO_OR_ONE".equals(cardinality))
			erd.append(parent ? "|o" : "o|");
		else if("EXACTLY_ONE".equals(cardinality))
			erd.append("||");
		else
			erd.append("||");
			
		if(parent)
			erd.append(dir == null ? "--" : "-" + dir + "-");
		
		return erd.toString();
	}
	
	/**
	 * @param image
	 * @return
	 */
	public static String imageToString(byte [] image)
	{
		StringBuffer str = new StringBuffer();
		
		str.append("data:image/png;base64, ");
		str.append(new String(Base64.getEncoder().encode(image)));

		return str.toString();
	}
	
	/**
	 * @param msg
	 * @return
	 */
	public static BufferedImage makeErrorImage(String msg)
	{
		BufferedImage img = new BufferedImage(800, 100, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 800, 100);
        g.setColor(Color.RED);
        g.drawString(msg, 10, 50);
        return img;
	}

	/**
	 * @return
	 */
	public static String now(boolean sep) {
		SimpleDateFormat sdf = new SimpleDateFormat (sep == true ? "yyyy-MM-dd HH:mm:ss" : "yyyyMMddHHmmss");
		return sdf.format(Calendar.getInstance().getTime());
	}
	
	/**
	 * @return
	 */
	public static String now() {
		return now(true);
	}
	
	/**
	 * @param day
	 * @param sep
	 * @return
	 */
	public static String addDay(int day, boolean sep) {
		SimpleDateFormat sdf = new SimpleDateFormat (sep == true ? "yyyy-MM-dd HH:mm:ss" : "yyyyMMddHHmmss");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, day);
		return sdf.format(c.getTime());
	}
	
	/**
	 * 
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public static String encryptPassword(String pwd) throws Exception
	{
		MessageDigest m = MessageDigest.getInstance("MD5");
	    m.update(pwd.getBytes(), 0, pwd.length());     
	    return new BigInteger(1, m.digest()).toString(16); 
	}
	
	/**
	 * @param tabCount
	 * @return
	 */
	public static String tab2Space(int tabCount) {
		return StringUtils.leftPad("", tabCount*4);
	}
	
	/**
	 * @param event
	 * @param handlerName
	 * @return
	 */
	public static String makeFuctionName(String event, String handlerName) {
		StringBuffer str = new StringBuffer();
		
		if(handlerName == null || handlerName.length() == 0)
			handlerName = "dummy";
			
		str.append(handlerName)
		   .append(event.substring(0, 1).toUpperCase())
		   .append(event.substring(1))
		   .append("Handler");
		
		return str.toString();
	}
}
