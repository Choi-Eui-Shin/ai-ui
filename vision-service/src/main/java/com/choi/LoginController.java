package com.choi;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.choi.vo.ChangePasswordVO;
import com.choi.vo.LoginVO;
import com.choi.vo.MetaVO;
import com.choi.vo.ResultVO;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/v1")
public class LoginController extends BaseRestController
{
	@Autowired
	private LoginService loginService;
	
	@Value("${auth.method}")
	private String authMethod;
	
	/**
	 * @param request
	 * @return
	 */
	@PostMapping("/users/auth")
	public ResponseEntity<ResultVO<MetaVO>> checkAuthMethod(HttpServletRequest request)
	{
		ResultVO<MetaVO> result = new ResultVO<>();
		
		try {
			MetaVO mv = new MetaVO();
			mv.setAuthMethod(authMethod);
			mv.setSiteCode("LLM");
			
			if("NONE".equals(authMethod)) {
				/*
				 * 로그인 없이 사용하는 경우 GUEST로 세션을 생성한다.
				 */
				LoginVO user = new LoginVO();
				user.setUserName("GUEST");
				user.setUserRole("USER");
				
				HttpSession session = request.getSession(true);
				session.setMaxInactiveInterval(30*60);
				session.setAttribute("loginUser", user);
			}
			
			result.setResult(mv);
			result.setReturnCode(true);
		}catch(Exception ig) {
			result.setReturnCode(false);
			result.setReturnMessage(ig.getMessage());
		}
		
		return new ResponseEntity<ResultVO<MetaVO>>(result, HttpStatus.OK);
	}
	
	
	@DeleteMapping("/users/{userId}/{password}")
	@ApiOperation(value = "사용자 삭제", notes = "사용자 삭제")
	public ResponseEntity<ResultVO<Void>> deleteUser(@PathVariable String userId, @PathVariable String password)
	{
		ResultVO<Void> result = new ResultVO<>();
		
		try {
			LoginVO parm = new LoginVO();
			parm.setUserId(userId);
			parm.setUserPassword(password);
			
			loginService.deleteUser(parm);
			result.setReturnCode(true);
		}catch(Exception ig) {
			result.setReturnCode(false);
			result.setReturnMessage(ig.getMessage());
		}
		
		return new ResponseEntity<ResultVO<Void>>(result, HttpStatus.OK);
	}
	
	@PutMapping("/users")
	@ApiOperation(value = "사용자 변경", notes = "사용자 변경")
	public ResponseEntity<ResultVO<LoginVO>> updateUser(@RequestBody LoginVO payload)
	{
		ResultVO<LoginVO> result = new ResultVO<>();
		
		try {
			result.setResult(loginService.updateUser(payload));
			result.setReturnCode(true);
		}catch(Exception ig) {
			result.setReturnCode(false);
			result.setReturnMessage(ig.getMessage());
		}
		
		return new ResponseEntity<ResultVO<LoginVO>>(result, HttpStatus.OK);
	}
	
	/**
	 * @param payload
	 * @return
	 */
	@PutMapping("/users/reset")
	@ApiOperation(value = "비밀번호 초기화", notes = "비밀번호 초기화")
	public ResponseEntity<ResultVO<LoginVO>> resetPassword(@RequestBody LoginVO payload)
	{
		ResultVO<LoginVO> result = new ResultVO<>();
		
		try {
			result.setResult(loginService.resetPassword(payload));
			result.setReturnCode(true);
		}catch(Exception ig) {
			result.setReturnCode(false);
			result.setReturnMessage(ig.getMessage());
		}
		
		return new ResponseEntity<ResultVO<LoginVO>>(result, HttpStatus.OK);
	}
	
	/**
	 * @param payload
	 * @return
	 */
	@PostMapping("/users")
	@ApiOperation(value = "사용자 등록", notes = "사용자 등록")
	public ResponseEntity<ResultVO<LoginVO>> addUser(@RequestBody LoginVO payload)
	{
		ResultVO<LoginVO> result = new ResultVO<>();
		
		try {
			result.setResult(loginService.addUser(payload));
			result.setReturnCode(true);
		}catch(Exception ig) {
			error(ig);
			result.setReturnCode(false);
			result.setReturnMessage(ig.getMessage());
		}
		
		return new ResponseEntity<ResultVO<LoginVO>>(result, HttpStatus.OK);
	}
	
	/**
	 * @return
	 */
	@GetMapping("/users")
	@ApiOperation(value = "사용자 목록 조회", notes = "사용자 목록 조회")
	public ResponseEntity<ResultVO<List<LoginVO>>> getUserList()
	{
		ResultVO<List<LoginVO>> result = new ResultVO<>();
		
		try {
			result.setReturnCode(true);
			result.setResult(loginService.getUserList());
		}catch(Exception ig) {
			error(ig);
			result.setReturnCode(false);
			result.setReturnMessage(ig.getMessage());
		}
		
		return new ResponseEntity<ResultVO<List<LoginVO>>>(result, HttpStatus.OK);
	}

	/**
	 * @param request
	 * @return
	 */
	@GetMapping("/users/design/logout")
	@ApiOperation(value = "로그아웃", notes = "로그아웃")
	public ResponseEntity<ResultVO<Void>> logout(HttpServletRequest request)
	{
		ResultVO<Void> result = new ResultVO<>();
		
		try {
			request.getSession(true).invalidate();
			result.setReturnCode(true);
		}catch(Exception ig) {
			error(ig);
			result.setReturnCode(false);
		}
		
		return new ResponseEntity<ResultVO<Void>>(result, HttpStatus.OK);
	}
	
	/**
	 * @param payload
	 * @return
	 */
	@PostMapping("/users/design/login")
	@ApiOperation(value = "로그인", notes = "로그인")
	public ResponseEntity<ResultVO<LoginVO>> login(@RequestBody LoginVO payload, HttpServletRequest request)
	{
		ResultVO<LoginVO> result = new ResultVO<>();
		try {
			boolean isOk = true;
			LoginVO rs = null;
			
			if("LDAP".equals(authMethod)) {
				/*
				 * LDAP + BASIC
				 */
				try {
					//BASIC
					rs = loginService.selectRole(payload.getUserId());
					if(rs == null) {
						rs = new LoginVO();
						rs.setUserId(payload.getUserId());
						rs.setUserName(payload.getUserName());
						rs.setUserRole("USER");
					}
				}catch(Exception ig) {
					isOk = false;
				}
			}
			else {
				/*
				 * BASIC
				 */
				rs = loginService.login(payload.getUserId(), Utils.encryptPassword(payload.getUserPassword()));
				if ( rs == null ) {
					isOk = false;
				}
			}
			
			if ( isOk ) {
				HttpSession session = request.getSession(true);
				session.setMaxInactiveInterval(60*60);
				session.setAttribute("loginUser", rs);
				
				result.setResult(rs);
				result.setReturnCode(true);
			}
			else {
				result.setReturnCode(false);
				result.setReturnMessage("Invalid User");
			}
		}catch(Exception e) {
			error(e);
			result.setReturnCode(false);
			result.setReturnMessage(e.getMessage());
		}
		
		return new ResponseEntity<ResultVO<LoginVO>>(result, HttpStatus.OK);
	}
	
	/**
	 * @param payload
	 * @return
	 */
	@PutMapping("/users/password")
	@ApiOperation(value = "비밀번호 변경", notes = "비밀번호 변경")
	public ResponseEntity<ResultVO<LoginVO>> changePassword(@SessionAttribute("loginUser") LoginVO user, @RequestBody ChangePasswordVO payload)
	{
		ResultVO<LoginVO> result = new ResultVO<>();
		
		try {
			loginService.changePassword(payload, user.getUserId());
			result.setReturnCode(true);
		}catch(Exception ig) {
			error(ig);
			result.setReturnCode(false);
			result.setReturnMessage(ig.getMessage());
		}
		
		return new ResponseEntity<ResultVO<LoginVO>>(result, HttpStatus.OK);
	}
	
	/**
	 * @param payload
	 * @return
	 */
	@PostMapping("/users/builder/login")
	@ApiOperation(value = "Cbp-Builder 로그인", notes = "Cbp-Builder 로그인")
	public ResponseEntity<ResultVO<LoginVO>> builderLogin(@RequestBody LoginVO payload, HttpServletRequest request)
	{
		ResultVO<LoginVO> result = new ResultVO<>();
		try {
			boolean isOk = true;
			/*
			 * BASIC
			 */
			LoginVO rs = loginService.login(payload.getUserId(), Utils.encryptPassword(payload.getUserPassword()));
			if ( rs == null ) {
				isOk = false;
			}
			
			if ( isOk ) {
				HttpSession session = request.getSession(true);
				session.setMaxInactiveInterval(30*60);
				session.setAttribute("loginUser", rs);
				
				result.setResult(rs);
				result.setReturnCode(true);
			}
			else {
				result.setReturnCode(false);
				result.setReturnMessage("Invalid User");
			}
		}catch(Exception e) {
			error(e);
			result.setReturnCode(false);
			result.setReturnMessage(e.getMessage());
		}
		
		return new ResponseEntity<ResultVO<LoginVO>>(result, HttpStatus.OK);
	}
	
	/**
	 * @param request
	 * @return
	 */
	@GetMapping("/users/builder/logout")
	@ApiOperation(value = "Cbp-Builder 로그아웃", notes = "Cbp-Builder 로그아웃")
	public ResponseEntity<ResultVO<Void>> builderLogout(HttpServletRequest request)
	{
		ResultVO<Void> result = new ResultVO<>();
		
		try {
			request.getSession(true).invalidate();
			result.setReturnCode(true);
		}catch(Exception ig) {
			error(ig);
			result.setReturnCode(false);
		}
		
		return new ResponseEntity<ResultVO<Void>>(result, HttpStatus.OK);
	}
	
}