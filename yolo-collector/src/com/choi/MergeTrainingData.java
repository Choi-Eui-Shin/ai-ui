package com.choi;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import javax.swing.JProgressBar;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class MergeTrainingData extends JPanel implements IExtractProgress
{
	private JTextField txtSrc;
	private JTextField txtOut;
	private JPanel panel;
	private JProgressBar progressBar;
	private JProgressBar zipProgress;
	private JTextField txtTarget;
	
	public MergeTrainingData() {
		setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		
		JButton cmdExtract = new JButton("데이터 추출");
		cmdExtract.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String infolder = txtSrc.getText();
				if(infolder.length() == 0) {
					Utils.showWarning(MergeTrainingData.this, "YOLO", "입력 폴더를 선택하세요");
					return;
				}
				
				final String outFolder = txtOut.getText();
				if(outFolder.length() == 0) {
					Utils.showWarning(MergeTrainingData.this, "YOLO", "출력 폴더를 선택하세요");
					return;
				}
				
				SwingWorker<Void, Void> work = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						// 버튼 처리
						_enableButtons(panel, false);
						Utils.working(MergeTrainingData.this);

						_extract(infolder, outFolder);
						
						// 버튼 처리
						Utils.completed(MergeTrainingData.this);
						_enableButtons(panel, true);
						
						
						return null;
					}
				};
				
				work.execute();
			}
		});
		panel.add(cmdExtract);
		
		JButton cmdGen = new JButton("라벨파일 생성");
		cmdGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String outFolder = txtOut.getText();
				if(outFolder.length() == 0) {
					Utils.showWarning(MergeTrainingData.this, "YOLO", "출력 폴더를 선택하세요");
					return;
				}
				
				SwingWorker<Void, Void> work = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						// 버튼 처리
						_enableButtons(panel, false);
						Utils.working(MergeTrainingData.this);
						
						YoloDao yoloDao = new YoloDao();
						yoloDao.init();

						ZipDumper zdr = new ZipDumper(null, outFolder, yoloDao);
						zdr.saveLabel();
						
						// 버튼 처리
						Utils.completed(MergeTrainingData.this);
						_enableButtons(panel, true);
						
						
						return null;
					}
				};
				
				work.execute();
			}
		});
		panel.add(cmdGen);
		
		JButton cmdClear = new JButton("CLEAR");
		cmdClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					_enableButtons(panel, false);
					Utils.working(MergeTrainingData.this);
					
					YoloDao yoloDao = new YoloDao();
					yoloDao.init();
					yoloDao.clear();
					
					Utils.completed(MergeTrainingData.this);
					_enableButtons(panel, true);
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		panel.add(cmdClear);
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblNewLabel = new JLabel("입력 폴더");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(10, 10, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_1.add(lblNewLabel, gbc_lblNewLabel);
		
		txtSrc = new JTextField();
		GridBagConstraints gbc_txtSrc = new GridBagConstraints();
		gbc_txtSrc.insets = new Insets(10, 0, 5, 5);
		gbc_txtSrc.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSrc.gridx = 1;
		gbc_txtSrc.gridy = 0;
		panel_1.add(txtSrc, gbc_txtSrc);
		txtSrc.setColumns(10);
		
		JButton btnNewButton_2 = new JButton("...");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String folder = Utils.showOpenFolder(MergeTrainingData.this);
				if(folder != null) {
					txtSrc.setText(folder);
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.insets = new Insets(10, 0, 5, 10);
		gbc_btnNewButton_2.gridx = 2;
		gbc_btnNewButton_2.gridy = 0;
		panel_1.add(btnNewButton_2, gbc_btnNewButton_2);
		
		JLabel lblNewLabel_1 = new JLabel("출력 폴더");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 10, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel_1.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		txtOut = new JTextField();
		GridBagConstraints gbc_txtOut = new GridBagConstraints();
		gbc_txtOut.insets = new Insets(0, 0, 5, 5);
		gbc_txtOut.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtOut.gridx = 1;
		gbc_txtOut.gridy = 1;
		panel_1.add(txtOut, gbc_txtOut);
		txtOut.setColumns(10);
		
		JButton btnNewButton_3 = new JButton("...");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String folder = Utils.showOpenFolder(MergeTrainingData.this);
				if(folder != null) {
					txtOut.setText(folder);
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
		gbc_btnNewButton_3.insets = new Insets(0, 0, 5, 10);
		gbc_btnNewButton_3.gridx = 2;
		gbc_btnNewButton_3.gridy = 1;
		panel_1.add(btnNewButton_3, gbc_btnNewButton_3);
		
		JLabel lblNewLabel_2 = new JLabel("전체 진행");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		panel_1.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		progressBar = new JProgressBar();
		progressBar.setMaximumSize(new Dimension(32767, 20));
		progressBar.setMinimumSize(new Dimension(10, 20));
		progressBar.setPreferredSize(new Dimension(146, 20));
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.insets = new Insets(0, 0, 5, 5);
		gbc_progressBar.gridx = 1;
		gbc_progressBar.gridy = 2;
		panel_1.add(progressBar, gbc_progressBar);
		
		JLabel lblNewLabel_3 = new JLabel("대상 파일");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		panel_1.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		txtTarget = new JTextField();
		txtTarget.setBackground(Color.WHITE);
		txtTarget.setEditable(false);
		GridBagConstraints gbc_txtTarget = new GridBagConstraints();
		gbc_txtTarget.insets = new Insets(0, 0, 5, 5);
		gbc_txtTarget.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTarget.gridx = 1;
		gbc_txtTarget.gridy = 3;
		panel_1.add(txtTarget, gbc_txtTarget);
		txtTarget.setColumns(10);
		
		zipProgress = new JProgressBar();
		zipProgress.setPreferredSize(new Dimension(146, 20));
		zipProgress.setMinimumSize(new Dimension(10, 20));
		zipProgress.setMaximumSize(new Dimension(32767, 20));
		GridBagConstraints gbc_zipProgress = new GridBagConstraints();
		gbc_zipProgress.fill = GridBagConstraints.HORIZONTAL;
		gbc_zipProgress.insets = new Insets(0, 0, 0, 5);
		gbc_zipProgress.gridx = 1;
		gbc_zipProgress.gridy = 4;
		panel_1.add(zipProgress, gbc_zipProgress);
	}
	
	/**
	 * @param src
	 * @param out
	 */
	private void _extract(String src, String out) {
		File root = new File(src);
		
		File [] targetList = root.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.toUpperCase().endsWith("ZIP"))
					return true;
				else
					return false;
			}
		});
		
		progressBar.setValue(0);
		progressBar.setMinimum(0);
		progressBar.setMaximum(targetList.length);
		
		YoloDao yoloDao = new YoloDao();
		try {
			yoloDao.init();
			yoloDao.clear();
			
			for(File file : targetList) {
				zipProgress.setValue(0);
				zipProgress.setMinimum(0);
				zipProgress.setMaximum(0);
				
				ZipDumper zdr = new ZipDumper(file.getAbsolutePath(), out, yoloDao);
				zdr.extract(this);
				progressBar.setValue(progressBar.getValue()+1);
			}
		} catch (YoloException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param root
	 * @param cntr
	 */
	private void _enableButtons(JPanel root, boolean cntr) {
		for(int i = 0; i < root.getComponentCount(); i++) {
			if(root.getComponent(i) instanceof JButton) {
				root.getComponent(i).setEnabled(cntr);
			}
		}
	}
	
	@Override
	public void process(String filename, int max, int value) {
		txtTarget.setText(filename);
		
		zipProgress.setMaximum(max);
		zipProgress.setValue(value);
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {}
		
		try {
			final MergeTrainingData gm = new MergeTrainingData();
			JFrame main = new JFrame();
			main.setTitle("YOLO 학습 이미지 추출");
			main.getContentPane().add(gm);
			main.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			main.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if ( JOptionPane.showConfirmDialog(gm, "종료 할까요?", "YOLO", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
			            System.exit(0);
					}
				}
			});

			main.pack();
			main.setSize(500, 220);
			Utils.locationCenter(main);
			main.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
