package com.choi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class YoloDao {
	private DBManager db = null;
	private PreparedStatement imagePst;
	private PreparedStatement labelPst;
	private PreparedStatement selectPst;
	
	public void init() throws YoloException
	{
		try {
			db = DBManager.getInstance();
			
			imagePst = db.getConnection().prepareStatement("INSERT INTO yolo VALUES (?, ?)");
			labelPst = db.getConnection().prepareStatement("INSERT INTO yolo_object VALUES (?, ?, ?, ?, ?, ?)");
			
			String selectSQL = "SELECT a.category\r\n"
					+ "   ,a.fileName\r\n"
					+ "	  ,b.classId\r\n"
					+ "	  ,b.bx_left\r\n"
					+ "	  ,b.bx_top\r\n"
					+ "	  ,b.bx_right\r\n"
					+ "	  ,b.bx_bottom\r\n"
					+ "FROM   yolo a,\r\n"
					+ "       yolo_object b\r\n"
					+ "WHERE  a.fileName = b.fileName\r\n"
					+ "ORDER BY a.category\r\n"
					+ "        ,a.fileName";
			
			selectPst = db.getConnection().prepareStatement(selectSQL);
		}catch(Exception e) {
			throw new YoloException(e);
		}
	}
	
	/**
	 * 사용 데이블 초기화
	 * 
	 * @throws YoloException
	 */
	public void clear() throws YoloException
	{
		try {
			db.getConnection().createStatement().execute("delete from yolo");
			db.getConnection().createStatement().execute("delete from yolo_object");
		}catch(Exception e) {
			throw new YoloException(e);
		}
	}
	
	/**
	 * @param category
	 * @param filename
	 */
	public void saveImage(String category, String filename) throws YoloException
	{
		try {
			imagePst.setString(1, category);
			imagePst.setString(2, filename);
			
			imagePst.execute();
		}catch(SQLException se) {
			throw new YoloException(se);
		}
	}
	
	/**
	 * @param filename
	 * @param classId
	 * @param lefe
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void saveLabel(String filename, String classId, String left, String top, String right, String bottom) throws YoloException
	{
		try {
			labelPst.setString(1, filename);
			labelPst.setString(2, classId);
			labelPst.setString(3, left);
			labelPst.setString(4, top);
			labelPst.setString(5, right);
			labelPst.setString(6, bottom);
			
			labelPst.execute();
		}catch(SQLException se) {
			throw new YoloException(se);
		}
	}
	
	/**
	 * @param handler
	 */
	public void fetchLabel(LabelHandler handler) throws YoloException
	{
		ResultSet rs = null;
		try {
			rs = selectPst.executeQuery();
			while(rs.next()) {
				handler.fetch(rs.getString("category"),
						rs.getString("fileName"),
						rs.getString("classId"),
						rs.getString("bx_left"),
						rs.getString("bx_top"),
						rs.getString("bx_right"),
						rs.getString("bx_bottom"));
			}
		}catch(SQLException se) {
			throw new YoloException(se);
		}finally {
			try {
				rs.close();
			}catch(Exception ig) {}
		}
	}
}
