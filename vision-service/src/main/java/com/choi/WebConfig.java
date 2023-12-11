package com.choi;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
public class WebConfig { // implements WebMvcConfigurer {
	/*
	 * 인증모드
	 */
	@Value("${auth.method}")
	private String authMethod;
	
	@Bean
	public FilterRegistrationBean<Filter> logFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new LoginCheckFilter("NONE".equals(authMethod) == false));
		filterRegistrationBean.setOrder(1); // 필터 체인할 때 가장 먼저 실행
		filterRegistrationBean.addUrlPatterns("/*"); // 모든 요청 url에 대해 실행
		return filterRegistrationBean;
	}

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		FileSystemResource dir = new FileSystemResource(pluginPath);
//		registry.addResourceHandler("/cbp-builder/**")
//				.addResourceLocations(dir)
//				.setCachePeriod(0);
//	}
	
	@Bean
	public HttpFirewall allowUrlEncodedPercentHttpFirewall() {
	    StrictHttpFirewall firewall = new StrictHttpFirewall();
	    firewall.setAllowUrlEncodedPercent(true);
	    return firewall;
	}
}
