package com.caixintech.wechat.cc.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "weixin.app")
public class WeChatConfig {
	private String appid;
	private String agentid;
	private String secret;
	private String wwserver;

	public void setAppid(String appid) {
		this.appid = appid;
	}
	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getAppid() {
		return appid;
	}
	public String getAgentid() {
		return agentid;
	}
	public String getSecret() {
		return secret;
	}
	public String getWwserver() {
		return wwserver;
	}
	public void setWwserver(String wwserver) {
		this.wwserver = wwserver;
	}
	
}
