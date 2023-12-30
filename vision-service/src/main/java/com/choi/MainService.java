package com.choi;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choi.core.IBuilder;
import com.choi.core.UiCodeGenerator;
import com.choi.core.VelocityGenerator;
import com.choi.core.VuetifyScreenBuilder;
import com.choi.core.VuetifyScriptBuilder;
import com.choi.entity.RuleDetail;
import com.choi.entity.RuleMaster;
import com.choi.entity.jpa.RuleDetailRepository;
import com.choi.entity.jpa.RuleMasterRepository;
import com.choi.ex.ServiceException;
import com.choi.vo.GenerateRequest;
import com.choi.vo.YoloObjectEntry;

/**
 * @author 최의신
 *
 */
@Service
public class MainService
{
	@Autowired
	private RuleDetailRepository ruleDetailRepository;
	
	@Autowired
	private RuleMasterRepository ruleMasterRepository;
	
	
	/**
	 * @param userId
	 * @return
	 */
	public List<RuleMaster> getTemplateList(String userId)
	{
		return ruleMasterRepository.getTemplageList(userId);
	}
	
	/**
	 * @param payload
	 * @return
	 * @throws ServiceException
	 */
	public String generate(GenerateRequest payload) throws ServiceException
	{
		Optional<RuleMaster> master = ruleMasterRepository.findById(payload.getTargetTemplateUuid());
		if(master.isEmpty())
			throw new ServiceException("등록된 정보가 없습니다.");
		/*
		 * TODO: 지정된 템플릿이 현재 사용자가 사용 가능한지 검사한다.
		 */
//		RuleMaster tm = master.get();
//		if("".equals(tm.getUserId()) == false)
//			throw new ServiceException("권한이 없습니다.");
		
		/*
		 * 지정된 템플릿의 룰 정보를 가져온다.
		 */
		List<RuleDetail> rules = ruleDetailRepository.getRule(payload.getTargetTemplateUuid());
		
		/*
		 * 화면 구성 스크립트 생성
		 */
		IBuilder screenBuilder = new VuetifyScreenBuilder();
		screenBuilder.setRule(rules);
		
		/*
		 * 자바 스크립트 생성
		 */
		IBuilder scriptBuilder = new VuetifyScriptBuilder();
		
		UiCodeGenerator uiGen = new UiCodeGenerator(rules, payload.getUiObjects(), screenBuilder, scriptBuilder);
		return uiGen.generateCode();
	}
	
	/**
	 * @param payload
	 * @return
	 * @throws ServiceException
	 */
	public byte [] generateBackend(GenerateRequest payload) throws ServiceException
	{
		byte [] zip = null;
		Optional<RuleMaster> master = ruleMasterRepository.findById(payload.getTargetTemplateUuid());
		if(master.isEmpty())
			throw new ServiceException("등록된 정보가 없습니다.");
		/*
		 * TODO: 지정된 템플릿이 현재 사용자가 사용 가능한지 검사한다.
		 */
//		RuleMaster tm = master.get();
//		if("".equals(tm.getUserId()) == false)
//			throw new ServiceException("권한이 없습니다.");
		
		/*
		 * 프로퍼티 추출
		 */
		Set<String> propList = new HashSet<>();
		for(YoloObjectEntry obj : payload.getUiObjects()) {
			String prop = obj.getPropertyName();
			if(prop != null && prop.trim().length() > 0)
				propList.add(prop);
		}
		
		try {
			VelocityGenerator vg = new VelocityGenerator();
			vg.put("StringUtils", new StringUtils());
			vg.put("myUtils", new Utils());
			
			/*
			 * 응답 압축파일
			 */
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ZipOutputStream zout = new ZipOutputStream(bo);
			
			/*
			 * Controller
			 */
			generateZip(vg, zout, "controller.vm", "ExampleController.java");
			
			/*
			 * Service
			 */
			generateZip(vg, zout, "service.vm", "ExampleService.java");
			
			/*
			 * Value Object
			 */
			vg.put("propertyList", propList);
			generateZip(vg, zout, "vo.vm", "ExampleVO.java");
			
			zout.close();
			
			zip = bo.toByteArray();
		}catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
		
		return zip;
	}
	
	/**
	 * @param vg
	 * @param zout
	 * @param vmFile
	 * @param fileName
	 * @throws Exception
	 */
	private void generateZip(VelocityGenerator vg, ZipOutputStream zout, String vmFile, String fileName) throws Exception
	{
		String code = vg.generate(vmFile);
		ZipEntry e = new ZipEntry(fileName);
		zout.putNextEntry(e);
		byte [] data = code.getBytes("utf-8");
		zout.write(data, 0, data.length);
		zout.closeEntry();
	}
}
