package com.choi;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.choi.vo.GenerateRequest;
import com.choi.vo.GenerateResponse;
import com.choi.vo.ResultVO;
import com.choi.vo.YoloResult;

import io.swagger.annotations.ApiOperation;

/**
 * @author 최의신
 *
 */
@RestController
@RequestMapping("/v1")
public class YoloController extends BaseRestController
{
	@Autowired
	private YoloService yoloService;
	
	@PostMapping("/vision/predict")
	@ApiOperation(value = "객체 인식", notes = "이미지에서 객체를 인식한다.")
	public ResponseEntity<ResultVO<YoloResult>> predict(@RequestParam("file") MultipartFile payload)
	{
		ResultVO<YoloResult> result = new ResultVO<>();
		File tmp = null;
		try {
			tmp = File.createTempFile("yolo", ".png");
			payload.transferTo(tmp);
			
			result.setResult(yoloService.predict(IOUtils.toByteArray(new FileInputStream(tmp))));
			result.setReturnCode(true);
		}catch(Exception e) {
			if(tmp != null) {
				tmp.delete();
			}
			error(e);
			result.setReturnCode(false);
			result.setReturnMessage(e.getMessage());
		}
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@PostMapping("/vision/predict")
	@ApiOperation(value = "소스코드 생성", notes = "주어진 정보로 소스코드를 생성한다.")
	public ResponseEntity<ResultVO<GenerateResponse>> generate(@RequestBody GenerateRequest payload)
	{
		ResultVO<GenerateResponse> result = new ResultVO<>();
		try {
			result.setReturnCode(true);
		}catch(Exception e) {
			error(e);
			result.setReturnCode(false);
			result.setReturnMessage(e.getMessage());
		}
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
