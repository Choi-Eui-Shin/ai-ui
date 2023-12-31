package com.choi;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
public class YoloService
{
	@Value(value="${yolo.service.url:http}")
	private String yoloServiceUrl;

	/**
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public YoloResult predict(byte [] image) throws Exception
	{
		YoloResult result = new YoloResult();
		
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
		    
		    YoloResult tmp = responseEntity.getBody();
		    
		    System.out.println("@.@ Time = " + (System.currentTimeMillis()-time + " msec"));
		    
		    if(!tmp.isReturnCode()) {
		    	throw new Exception("YOLO 서비스 오류!");
		    }
		    
		    result.setSourceImage(imageToString(image));
		    result.setImageWidth(tmp.getImageWidth());
		    result.setImageHeight(tmp.getImageHeight());
		    
		    /*
		     * 특정 클래스 제외
		     */
		    List<YoloObjectEntry> finalList = tmp.getResult().stream().filter(m -> "screen".equals(m.getClassId()) == false).collect(Collectors.toList());
		    result.setResult(new ArrayList<>());
		    result.getResult().addAll(finalList);
		    
		    /*
		     * 모든 객체에 번호를 부여한다.
		     */
		    int cap = result.getImageWidth()/3;
		    int num = 1;
		    for(YoloObjectEntry ue : finalList) {
		    	ue.setDepth(1);
		    	ue.setNumber(num++);
		    	
		    	// 이미지 전체 크기에서 3등분하여 객체의 위치를 결정한다. (LEFT, MIDDLE, RIGHT)
		    	if(ue.getRect().getX() <= cap)
		    		ue.setPosition("left");
		    	else if(cap < ue.getRect().getX() && ue.getRect().getX() <= (cap*2))
		    		ue.setPosition("middle");
		    	else if(ue.getRect().getX() > (cap*2))
		    		ue.setPosition("right");
		    }
		    
		    /*
		     * 경계박스의 크기로 내림차순 정렬
		     */
		    result.getResult().sort(new Comparator<YoloObjectEntry>() {
				@Override
				public int compare(YoloObjectEntry o1, YoloObjectEntry o2) {
					int a1 = o1.area();
					int a2 = o2.area();
					return a1 == a2 ? 0 : a1 > a2 ? -1 : 1;
				}
			});
		    
		    /*
		     * 영역이 포함 관계에 있는지 검사한다. 
		     */
//		    List<YoloObjectEntry> allList = result.getResult();
//		    for(int i = 0; i < allList.size()-1; i++) {
//		    	YoloObjectEntry base = allList.get(i);
//		    	
//		    	for(int s = i+1; s < allList.size(); s++) {
//		    		YoloObjectEntry ct = allList.get(s);
//		    		
//		    		Rectangle intersection = base.getRect().intersection(ct.getRect());
//		    		if(intersection != null) {
//		    			if((double)intersection.area()/(double)base.area() >= 1.0) {
////		    				System.out.println("@ " + i + "/" + s + " = " + ((double)intersection.area() / (double)base.area()));
//		    				ct.setDepth(base.getDepth()+1);
//		    				base.setParentNumber(ct.getNumber());
//		    				break;
//		    			}
//		    		}
//		    	}
//		    }
		    
		    /*
		     * 영역이 포함 관계에 있는지 검사한다. 
		     */
		    List<YoloObjectEntry> allList = result.getResult();
		    for(int i = 0; i < allList.size()-1; i++) {
		    	YoloObjectEntry base = allList.get(i);
		    	
		    	for(int s = i+1; s < allList.size(); s++) {
		    		YoloObjectEntry ct = allList.get(s);
		    		// 겹치는 영역을 추출한다.
		    		Rectangle intersection = base.getRect().intersection(ct.getRect());
		    		if(intersection != null) {
		    			if((double)ct.area()/(double)intersection.area() == 1.0) {
//		    				System.out.println("@ %d-%d/%d-%d = %f".formatted(i, base.getNumber(), s, ct.getParentNumber(),(double)ct.area()/(double)intersection.area())); 
		    				ct.setDepth(base.getDepth()+1);
		    				ct.setParentNumber(base.getNumber());
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
