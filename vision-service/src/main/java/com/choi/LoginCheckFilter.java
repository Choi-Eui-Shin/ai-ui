package com.choi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.PatternMatchUtils;

public class LoginCheckFilter implements Filter
{
	class CleanThread extends Thread
	{
		public boolean shutdown = false;
		
		public void run() {
			while(shutdown == false) {
				try {
					Thread.sleep(1800*1000); 	// 30분마다 실행

					for(Iterator<String> ip = accessList.keySet().iterator(); ip.hasNext(); ) {
						String key = ip.next();
						Long t = accessList.get(key);
						if ( System.currentTimeMillis() - t.longValue() > (3600*1000*2) ) {	// 2시간이 지나면 삭제
							accessList.remove(key);
						}
					}
				}catch(Exception e) {
				}
			}
		}
	}
	
	private CleanThread cleanThread;
	private Map<String, Long> accessList = new HashMap<>();
	
	private boolean checkAuth = true;
	
	public LoginCheckFilter(boolean auth) {
		this.checkAuth = auth;
		this.cleanThread = new CleanThread();
		this.cleanThread.start();
	}
	
	private static final String[] whitelist = { "/", "/favicon.ico", "/js/**", "/img/**", "/css/**",
            "/v1/users/design/login", "/v1/users/design/logout", "/v1/users/builder/login", "/v1/users/builder/logout",
            "/v1/users/auth", "/transationList" };

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String requestURI = httpRequest.getRequestURI();

		try {
			if (checkAuth && isLoginCheckPath(requestURI)) {
				HttpSession session = httpRequest.getSession(false);
				if (session == null || session.getAttribute("loginUser") == null) {
					/*
					 * HTTP 403을 전송하여 로그인을 유도한다.
					 */
					httpResponse.setStatus(403);
					return;
				}
			}
			
			if(requestURI.contains("transationList")) {
				String nameFilter = request.getParameter("nameFilter");
				if(nameFilter != null) {
					request.setAttribute("nameFilter", "DDDD");
				}
			}
			
			chain.doFilter(request, response);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 화이트 리스트의 경우 인증 체크X
	 */
	private boolean isLoginCheckPath(String requestURI) {
		return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
	}
	
	/**
	 *
	 */
	public void destroy() {
		cleanThread.shutdown = true;
		cleanThread.interrupt();
	}
}