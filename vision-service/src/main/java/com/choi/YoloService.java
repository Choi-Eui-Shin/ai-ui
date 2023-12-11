package com.choi;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.choi.vo.Rectangle;
import com.choi.vo.YoloObjectEntry;
import com.choi.vo.YoloResult;

/**
 * @author 최의신
 *
 */
@Service
public class YoloService {
	@Value(value="${yolo.service.url:http}")
	private String yoloServiceUrl;
	
	/**
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public YoloResult predict(byte [] image) throws Exception
	{
		YoloResult result = null;
		
		if("http".equals(yoloServiceUrl) == false) {
			RestTemplate restTemplate = new RestTemplate();
			
			ByteArrayResource contentsAsResource = new ByteArrayResource(image) {
		        @Override
		        public String getFilename() {
		            return "image.png";
		        }
		    };
			
		    long time = System.currentTimeMillis();
		    ResponseEntity<YoloResult> responseEntity = restTemplate.postForEntity(
		    		yoloServiceUrl + "/v1/yolov8",
					contentsAsResource,
					YoloResult.class);
		    result = responseEntity.getBody();
		    
		    System.out.println("@.@ Time = " + (System.currentTimeMillis()-time + " msec"));
		    
		    if(!result.isReturnCode()) {
		    	throw new Exception("YOLO 서비스 오류!");
		    }
		    
		    result.setSourceImage(imageToString(image));
		    /*
		     * 경계박스의 크기로 정렬
		     */
		    result.getResult().sort(new Comparator<YoloObjectEntry>() {
				@Override
				public int compare(YoloObjectEntry o1, YoloObjectEntry o2) {
					int a1 = o1.area();
					int a2 = o2.area();
					return a1 == a2 ? 0 : a1 > a2 ? 1 : -1;
				}
			});
		    
		    /*
		     * 영역이 포함 관계에 있는지 검사한다. 
		     */
		    List<YoloObjectEntry> allList = result.getResult();
		    for(int i = 0; i < allList.size()-1; i++) {
		    	YoloObjectEntry base = allList.get(i);
		    	
		    	for(int s = i+1; s < allList.size(); s++) {
		    		YoloObjectEntry ct = allList.get(s);
		    		
		    		Rectangle intersection = base.getRect().intersection(ct.getRect());
		    		if(intersection != null) {
		    			if(intersection.area()/base.area() >= 1) {
		    				int intersectionArea = intersection.area();
		    				int ctArea = base.area();
		    				System.out.println("@ " + i + "/" + s + " = " + (intersectionArea / ctArea));
		    				ct.setDepth(ct.getDepth()+1);
		    			}
		    		}
		    	}
		    }
		}
		else {
			throw new Exception("YOLO 서비스 정보가 없습니다.");
		}
		
		return result;
	}
	
	/**
	 * 이미지 배열을 문자열로 변환한다.
	 * 
	 * @param image
	 * @return
	 */
	private String imageToString(byte [] image)
	{
		StringBuffer str = new StringBuffer();
		
		str.append("data:image/png;base64, ");
		str.append(new String(Base64.getEncoder().encode(image)));

		return str.toString();
	}
}
