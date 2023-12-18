package com.choi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

/**
 * https://github.com/srikanth-lingala/zip4j
 * 
 * @author 최의신
 *
 */
public class ZipDumper {

	private String zipFile;
	private String outputFolder;
	private YoloDao yoloDao;
	
	public ZipDumper(String zipFile, String out, YoloDao yoloDao) {
		this.zipFile = zipFile;
		this.outputFolder = out;
		this.yoloDao = yoloDao;
		
		try {
			File rootDir = new File(out);
			File trainDir = new File(rootDir, "train");
			File testDir = new File(rootDir, "test");
			File validDir = new File(rootDir, "valid");
			
			// train
			File imageDir = new File(trainDir, "images");
			imageDir.mkdirs();
			File labelDir = new File(trainDir, "labels");
			labelDir.mkdirs();
			
			// test
			imageDir = new File(testDir, "images");
			imageDir.mkdirs();
			labelDir = new File(testDir, "labels");
			labelDir.mkdirs();
			
			// valid
			imageDir = new File(validDir, "images");
			imageDir.mkdirs();
			labelDir = new File(validDir, "labels");
			labelDir.mkdirs();
		}catch(Exception e) {
		}
	}
	
	/**
	 * @return
	 */
	public boolean extract(IExtractProgress monitor) {
		boolean rs = false;
		
		ZipFile zip = new ZipFile(zipFile);
		try {
			/*
			 * data.yaml 파일에서 클래스를 추출한다.
			 */
			List<String> classList = getClassList(zip);
			
			/*
			 * 이미지와 라벨을 추출하고 정보를 데이터베이스에 저장한다.
			 */
			List<FileHeader> allFiles = zip.getFileHeaders();
			allFiles = allFiles.stream().filter(
					m -> m.getFileName().startsWith("train") ||
						m.getFileName().startsWith("test") ||
						m.getFileName().startsWith("valid")).toList();
			
			for(int i = 0; i < allFiles.size(); i++) {
				FileHeader fh = allFiles.get(i);
				/*
				 * part[0] - category: train, valid, test
				 * part[1] - images , labels
				 * part[2] - filename
				 */
				String [] part = fh.getFileName().split("/");
				if(part.length != 3)
					continue;
				
				String id = part[2].substring(0, part[2].lastIndexOf("."));
				
				if(monitor != null)
					monitor.process(id, allFiles.size(), i+1);

				if(part[2].endsWith("txt")) {
					/*
					 * 라벨 데이터는 내용을 읽어 데이터베이스에 저장한다.
					 */
					String content = IOUtils.toString(zip.getInputStream(fh), "utf-8");
					BufferedReader br = null;
					try {
						br = new BufferedReader(new StringReader(content));
						String line = null;
						
						do {
							line = br.readLine();
							if(line != null) {
								String [] tks = line.split(" ");
								yoloDao.saveLabel(id, classList.get(_toInt(tks[0])), tks[1], tks[2], tks[3], tks[4]);
							}
						}while(line != null);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				else {
					yoloDao.saveImage(part[0], id);
					
					/*
					 * 이미지는 지정된 폴더에 파일로 생성한다.
					 */
					Path path = Paths.get(outputFolder, part[0], part[1], part[2]);
					File targetFile = path.toFile();
					try (FileOutputStream fo = new FileOutputStream(targetFile)) {
						IOUtils.copy(zip.getInputStream(fh), fo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	/**
	 * @return
	 */
	public boolean saveLabel() {
		boolean rs = false;
		
		YoloDao yoloDao = null;
		
		try {
			yoloDao = new YoloDao();
			yoloDao.init();
			Map<String, Integer> mapper = yoloDao.getClassMapper();
			
			LabelWriter lw = new LabelWriter(outputFolder, mapper);
			yoloDao.fetchLabel(lw);
			lw.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	/**
	 * @param str
	 * @return
	 */
	private int _toInt(String str) {
		int v = -1;
		try {
			v = Integer.parseInt(str);
		}catch(Exception e) {}
		
		return v;
	}
	
	/**
	 * @param zip
	 * @return
	 * @throws Exception
	 */
	private List<String> getClassList(ZipFile zip) throws Exception
	{
		FileHeader fileHeader = zip.getFileHeader("data.yaml");
		String content = IOUtils.toString(zip.getInputStream(fileHeader), "utf-8");
		return Utils.getClassList(content);
	}
	
	public static void main(String [] args) {
		try {
			YoloDao yoloDao = new YoloDao();
			yoloDao.init();
			yoloDao.clear();
			/*
				Html Merged Comps.v4i.yolov8.zip: 640x640
				Projeto_2.v1i.yolov8.zip: 640x640
				Sketch2aia - Dataset 3.v5-auto-orient-no-aug.yolov8: 가변
				Sketch2aia - New Dataset.v6-640x640-remapped-slightly-rotated.yolov8: 640 x 640
				Sketch2Code.v3i.yolov8: 640 x 640
				Sketch2Penpot.v1i.yolov8: 가변
			*/
			ZipDumper zdr = new ZipDumper("C:\\PerfLogs\\Sketch2Code.v3i.yolov8.zip", "C:\\PerfLogs", yoloDao);
//			ZipDumper zdr = new ZipDumper("C:\\PerfLogs\\Html Merged Comps.v4i.yolov8.zip", "C:\\PerfLogs", yoloDao);
//			ZipDumper zdr = new ZipDumper("C:\\PerfLogs\\Projeto_2.v1i.yolov8.zip", "C:\\PerfLogs", yoloDao);
//			ZipDumper zdr = new ZipDumper("C:\\PerfLogs\\Sketch2aia - Dataset 3.v5-auto-orient-no-aug.yolov8.zip", "C:\\PerfLogs", yoloDao);
//			ZipDumper zdr = new ZipDumper("C:\\PerfLogs\\Sketch2aia - New Dataset.v6-640x640-remapped-slightly-rotated.yolov8.zip", "C:\\PerfLogs", yoloDao);
//			ZipDumper zdr = new ZipDumper("C:\\PerfLogs\\Sketch2Penpot.v1i.yolov8.zip", "C:\\PerfLogs", yoloDao);
			zdr.extract(new IExtractProgress() {
				@Override
				public void process(String filename, int max, int value) {
					System.out.println(filename);
				}
			});
//			zdr.saveLabel();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
