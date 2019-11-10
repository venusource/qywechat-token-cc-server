package com.caixintech.wechat.cc.server.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.caixintech.wechat.cc.server.WeChatConfig;
import com.caixintech.wechat.cc.server.dto.WechatAccessTokenDto;
import com.caixintech.wechat.cc.server.dto.WechatTicketDto;
import com.caixintech.wechat.cc.server.util.HttpClientUtil;
import com.google.gson.Gson;

@Service
public class WechatServerService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Gson gson = new Gson();

	@Autowired
	WeChatConfig config;

	private String accessToken;

	private String jssdkTicket;

	private String agentTicket;

	@PostConstruct
	@Scheduled(cron = "0 0 0/2 * * ?")
	public void updateAccessTokenAndJssdkTicket() throws Exception {
		WechatAccessTokenDto watd = fetchAccessToken();
		if (watd.getErrcode() == 0) {
			setAccessToken(watd.getAccess_token());
		}
		WechatTicketDto wtd = fetchTicket();
		if (wtd.getErrcode() == 0) {
			setJssdkTicket(wtd.getTicket());
		}

		WechatTicketDto agentWtd = fetchAgentTicket();
		if (agentWtd.getErrcode() == 0) {
			setAgentTicket(agentWtd.getTicket());
		}
	}

	public Map<String, String> sign(String url) {
		return sign(url, getJssdkTicket());
	}

	public Map<String, String> signAgentUrl(String url) {
		return sign(url, getAgentTicket());
	}

	private Map<String, String> sign(String url, String ticket) {
		Map<String, String> ret = new HashMap<String, String>();
		String nonce_str = create_nonce_str();
		String timestamp = create_timestamp();
		String string1;
		String signature = "";

		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
		// System.out.println(string1);

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		ret.put("appId", config.getAppid());
		ret.put("agentId", config.getAgentid());
		ret.put("jsapi_ticket", getJssdkTicket());
		ret.put("nonceStr", nonce_str);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		return ret;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	private static String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

	private static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	private WechatAccessTokenDto fetchAccessToken() throws Exception {
		String url = config.getWwserver() + "/cgi-bin/gettoken?corpid=" + config.getAppid() + "&corpsecret="
				+ config.getSecret();
		String result = HttpClientUtil.httpGet(url, "UTF-8");
		logger.debug("getAccessToken origin result:{}", result);
		return gson.fromJson(result, WechatAccessTokenDto.class);
	}

	private WechatTicketDto fetchTicket() throws Exception {
		logger.debug("更新tickets");
		String accessToken = getAccessToken();
		String url = config.getWwserver() + "/cgi-bin/get_jsapi_ticket?access_token=" + accessToken;
		String result = HttpClientUtil.httpGet(url, "UTF-8");
		logger.debug("getTicket origin result:{}", result);
		return gson.fromJson(result, WechatTicketDto.class);
	}

	private WechatTicketDto fetchAgentTicket() throws Exception {
		logger.debug("更新agent tickets");
		String accessToken = getAccessToken();
		String url = config.getWwserver() + "/cgi-bin/ticket/get?access_token=" + accessToken + "&type=agent_config";
		String result = HttpClientUtil.httpGet(url, "UTF-8");
		logger.debug("getAgentTicket origin result:{}", result);
		return gson.fromJson(result, WechatTicketDto.class);
	}

	public String getJssdkTicket() {
		return jssdkTicket;
	}

	public void setJssdkTicket(String jssdkTicket) {
		this.jssdkTicket = jssdkTicket;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAgentTicket() {
		return agentTicket;
	}

	public void setAgentTicket(String agentTicket) {
		this.agentTicket = agentTicket;
	}

}
