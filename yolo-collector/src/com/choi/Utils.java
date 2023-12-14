package com.choi;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

@SuppressWarnings("deprecation")
public class Utils {
	private static JFileChooser folder = new JFileChooser();
	
	/**
	 * @param comp
	 * @param title
	 * @param message
	 */
	public static void showWarning(Component comp, String title, String message) {
		JOptionPane.showMessageDialog(getRoot(comp), message, title, JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * @param comp
	 * @param title
	 * @param message
	 */
	public static void showInformation(Component comp, String title, String message) {
		JOptionPane.showMessageDialog(getRoot(comp), message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * @param comp
	 * @param title
	 * @param message
	 */
	public static void showError(Component comp, String title, String message) {
		JOptionPane.showMessageDialog(getRoot(comp), message, title, JOptionPane.ERROR_MESSAGE);
	}	
	
	public static void working(java.awt.Component comp) {
		getRoot(comp).setCursor(Cursor.WAIT_CURSOR);
	}

	public static void completed(java.awt.Component comp) {
		getRoot(comp).setCursor(Cursor.DEFAULT_CURSOR);
	}
	
	public static javax.swing.JFrame getRoot(java.awt.Component comp) {
		javax.swing.JFrame frm = null;

		while (comp != null) {
			if (comp instanceof javax.swing.JFrame) {
				frm = (javax.swing.JFrame) comp;
				break;
			} else {
				comp = comp.getParent();
			}
		}

		if (frm == null)
			frm = new javax.swing.JFrame();

		return frm;
	}
	
	/**
	 * @param parent
	 * @return
	 */
	public static String showOpenFolder(Component parent) {
		folder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		f.setCurrentDirectory(new java.io.File("C:\\"));
		if (folder.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION)
			return null;

		return folder.getSelectedFile().getPath();
	}
	
	/**
	 * @param target
	 */
	public static void locationCenter(Window target) {
		Dimension size = target.getSize();
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - size.width) / 2;
		int y = (screen.height - size.height) / 2;

		target.setLocation(x, y);
	}
	
	/**
	 * @param text
	 * @return
	 */
	public static List<String> getClassList(String text) {
		List<String> classList = new ArrayList<>();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new StringReader(text));
			String line = null;
			
			do {
				line = br.readLine();
				if(line != null) {
					if(line.startsWith("names:")) {
						String [] tks = line.substring(6).split(",");
						for(String s : tks) {
							classList.add(s.replaceAll("[ '\\[\\]]", ""));
						}
					}
				}
			}while(line != null);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return classList;
	}
}
