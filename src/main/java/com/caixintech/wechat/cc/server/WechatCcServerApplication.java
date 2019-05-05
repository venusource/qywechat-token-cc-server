package com.caixintech.wechat.cc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
@EnableScheduling
public class WechatCcServerApplication {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${weixin.app.security.domains}")
	private String securityDomains;

	public static void main(String[] args) {
		SpringApplication.run(WechatCcServerApplication.class, args);
	}
	
	@Bean
	public FilterRegistrationBean corsFilter() {
		String[] securityDomainsArray = securityDomains.split(",");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		for (String domain : securityDomainsArray) {
			logger.info("loading security domain:{}",domain);
			config.addAllowedOrigin(domain);
		}
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);

		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(0);
		return bean;

	}
	
}
