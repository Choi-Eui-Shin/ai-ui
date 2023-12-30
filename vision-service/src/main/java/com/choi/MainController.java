package com.choi;


import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choi.entity.RuleMaster;
import com.choi.vo.GenerateRequest;
import com.choi.vo.GenerateResponse;
import com.choi.vo.ResultVO;

import io.swagger.annotations.ApiOperation;

/**
 * @author 최의신
 *
 */
@RestController
@RequestMapping("/v1")
public class MainController extends BaseRestController
{
	@Autowired
	private MainService mainService;
	
	
	/**
	 * @param payload
	 * @return
	 */
	@GetMapping("/main/template")
	@ApiOperation(value = "템플릿 목록", notes = "등록된 템플릿 목록을 조회한다.")
	public ResponseEntity<ResultVO<List<RuleMaster>>> getTemplateList()
	{
		ResultVO<List<RuleMaster>> result = new ResultVO<>();
		try {
			// TODO: 로그인 사용자 아이디 적용
			result.setResult(mainService.getTemplateList("TODO"));
			result.setReturnCode(true);
		}catch(Exception e) {
			error(e);
			result.setReturnCode(false);
			result.setReturnMessage(e.getMessage());
		}
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	
	
	/**
	 * @param payload
	 * @return
	 */
	@PostMapping("/main/generate")
	@ApiOperation(value = "소스코드 생성", notes = "주어진 정보로 소스코드를 생성한다.")
	public ResponseEntity<ResultVO<GenerateResponse>> generate(@RequestBody GenerateRequest payload)
	{
		ResultVO<GenerateResponse> result = new ResultVO<>();
		try {
			GenerateResponse rs = new GenerateResponse();
			rs.setSourceCode(mainService.generate(payload));
			
			result.setResult(rs);
			result.setReturnCode(true);
		}catch(Exception e) {
			error(e);
			result.setReturnCode(false);
			result.setReturnMessage(e.getMessage());
		}
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * @param payload
	 * @return
	 */
	@PostMapping("/main/{downloadType}/download")
	@ApiOperation(value = "소스코드 다운로드", notes = "소스코드 다운로드")
	public ResponseEntity<?> download(@PathVariable String downloadType, @RequestBody GenerateRequest payload)
	{
		try {
			Resource resource = null;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

			if("backend".equals(downloadType)) {
				resource = new ByteArrayResource(mainService.generateBackend(payload));
				headers.setContentDisposition(ContentDisposition.builder("attachment").filename("gen_backend_" + Utils.now(false) + ".zip").build());
			}
			else {
				// frontend
				String front = mainService.generate(payload);
				resource = new ByteArrayResource(front.getBytes("utf-8"));
				headers.setContentDisposition(ContentDisposition.builder("attachment").filename("gen_ui_" + Utils.now(false) + ".vue").build());
			}
			
			return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
		}catch(Exception e) {
			error(e);
			Resource resource = null;
			try {
				resource = new ByteArrayResource(e.getMessage().getBytes("utf-8"));
			} catch (UnsupportedEncodingException e1) {
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(ContentDisposition.builder("attachment").filename("error.txt").build());
			return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
		}
	}
}
