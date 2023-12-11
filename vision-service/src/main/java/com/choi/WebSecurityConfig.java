package com.choi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	}

//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		web.ignoring().antMatchers("/favicon.ico", "/logo.png", "/style.css", "/sky2.png");
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
			.antMatchers("/v1/**").permitAll()
//        .antMatchers(HttpMethod.GET, "/users")
//        .hasRole("ADMIN")
//        .antMatchers(HttpMethod.GET, "/users/{email}")
//        .hasAnyRole("ADMIN", "USER")
//        .antMatchers(HttpMethod.POST, "/users")
//        .permitAll()
//        .antMatchers(HttpMethod.PUT, "/users")
//        .hasAnyRole("ADMIN", "USER")
//        .antMatchers(HttpMethod.DELETE, "/users")
//        .hasAnyRole("ADMIN", "USER")
//        .antMatchers("/groups/**")
//        .hasRole("ADMIN")
			.and().exceptionHandling()
			
//			.hasAnyRole("ADMIN", "USER").and().exceptionHandling()
//				.permitAll().and().exceptionHandling()
//        .authenticationEntryPoint(ldapTokenAuthenticationEntryPoint)
//        .accessDeniedHandler(ldapAccessDeniedHandler)
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//        .addFilterBefore(ldapTokenFilter, UsernamePasswordAuthenticationFilter.class)
				.formLogin().disable();
//		
//		http
//			.csrf().disable()
//	        .authorizeRequests()
//	        .anyRequest().fullyAuthenticated()
//	        .and()
//	        .formLogin()
//	        	.loginPage("/login")
//	        	.permitAll()
//			.and()
//			.logout().permitAll()
//			.and()
//			.rememberMe()
//				.tokenRepository(new InMemoryTokenRepositoryImpl())	        
//	        ;
	}
}
