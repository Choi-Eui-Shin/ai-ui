package com.choi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choi.vo.ChangePasswordVO;
import com.choi.vo.LoginVO;

/**
 * @author 최의신
 *
 */
@Service
public class LoginService {
//	@Autowired
//	private UsersDao usersDao;
	
	/**
	 * 로그인
	 * @param userId
	 * @param password
	 * @return
	 */
	public LoginVO login(String userId, String password)
	{
		Map<String, String> parm = new HashMap<>();
		parm.put("userId", userId);
		parm.put("userPassword", password);
		
		return new LoginVO(); //usersDao.login(parm);
	}
	
	/**
	 * @param username
	 * @return
	 */
	public LoginVO selectRole(String userId)
	{
		Map<String, String> parm = new HashMap<>();
		parm.put("userId", userId);
		
		return null; //usersDao.selectRole(parm);
	}
	
	/**
	 * @return
	 */
	public List<LoginVO> getUserList()
	{
		return null; //usersDao.selectUsers();
	}
	
	/**
	 * @param payload
	 * @return
	 */
	public LoginVO addUser(LoginVO payload) throws Exception
	{
		payload.setUserPassword(Utils.encryptPassword(payload.getUserPassword()));
//		usersDao.insertUser(payload);
		return payload;
	}
	
	/**
	 * @param payload
	 * @return
	 */
	public LoginVO updateUser(LoginVO payload) throws Exception
	{
//		usersDao.updateUser(payload);
		return payload;
	}
	
	/**
	 * @param payload
	 * @return
	 * @throws Exception
	 */
	public LoginVO resetPassword(LoginVO payload) throws Exception
	{
		payload.setUserPassword(Utils.encryptPassword(payload.getUserPassword()));
//		usersDao.resetPassword(payload);
		return payload;
	}
	
	/**
	 * @param payload
	 * @throws Exception
	 */
	public void deleteUser(LoginVO payload) throws Exception
	{
//		usersDao.deleteUser(payload);
	}
	
	/**
	 * @param payload
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public void changePassword(ChangePasswordVO payload, String userId) throws Exception
	{
		LoginVO old = login(userId, Utils.encryptPassword(payload.getOldPassword()));
		if(old == null)
			throw new Exception("이전 비밀번호가 틀립니다.");

		LoginVO newPwd = new LoginVO();
		newPwd.setUserId(userId);
		newPwd.setUserPassword(payload.getNewPassword());
		resetPassword(newPwd);
	}
	
}
