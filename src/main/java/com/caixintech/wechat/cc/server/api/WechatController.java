package com.caixintech.wechat.cc.server.api;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.caixintech.wechat.cc.server.service.WechatServerService;

@RestController
@RequestMapping("/wechat")
public class WechatController {
	
	@Autowired
	WechatServerService wechatServerService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	/**
	 * JS-SDK使用URL签名算法 详见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141115
	 * 
	 * @param url 需要签名的URL
	 * @return 返回如下json：
	 * {"signature":"a935ba6425af0200f9c1d235afceb226c8aa943e",
	 * "appId":"wx809541fa44664742",
	 * "jsapi_ticket":"HoagFKDcsGMVCIY2vOjf9gp3KggvsLK2CZlPyty3efYZ7mjaoVTAZzQnP-mrf8ih-hih3vq-0vS8WIMr6xXvzA",
	 * "url":"http://wxtest.devincloud.cn/",
	 * "nonceStr":"c9a6017c-bb34-4339-a3c1-7430e1fbd7ea",
	 * "timestamp":"1499067696"}
	 */
	@RequestMapping(value = "/sign", method = RequestMethod.POST)
	public ResponseEntity<?> sign(@RequestParam String url){
		logger.debug("request jssdk sign:{}",url);
		try {
			return ResponseEntity.ok(wechatServerService.sign(url));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	
	/**
	 * agentConfig的作用 
	 * config注入的是企业的身份与权限，而agentConfig注入的是应用的身份与权限。尤其是当调用者为第三方服务商时，
	 * 通过config无法准确区分出调用者是哪个第三方应用，而在部分场景下，又必须严谨区分出第三方应用的身份，
	 * 此时即需要通过agentConfig来注入应用的身份信息。
	 * agentConfig与config的签名算法完全一样，但是jsapi_ticket的获取方法不一样，请特别注意，查看附录5
	 * 调用wx.agentConfig之前，必须确保先成功调用wx.config
	 * 当前页面url中的域名必须是在该应用中设置的可信域名。
	 * agentConfig仅在企业微信1.3.5及以后版本支持。
	 * agentConfig是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在回调函数中调用来确保正确执行。
	 * 对于用户触发时才调用的接口，则可以直接调用，不需要放在回调函数中。（同一个url仅需调用一次，对于变化url的SPA的web app可在每次url变化时进行调用。
	 * @param url
	 * @return
	 */
	@RequestMapping(value = "/agentSign", method = RequestMethod.POST)
	public ResponseEntity<?> agentSign(@RequestParam String url){
		logger.debug("request jssdk sign:{}",url);
		try {
			return ResponseEntity.ok(wechatServerService.signAgentUrl(url));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	/**
	 * 获取Access_token，详见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140183
	 * @return 返回access token，格式如下:
	 * {"token":"vx-z0qrVAlJR3E7neCH9U8kwX3Jvgmif5cPMm_Usxs22OUhzR3GKtkM2Q6t4XDmRKWfYslRHZIqNzIm6SX1LmYE9WhRzl4f0oqaFpB6hE-wk3PcjHGQKGF7yrF-2G9j8WJOdABAIVA"}
	 */
	@RequestMapping(value = "/accesstoken", method = RequestMethod.GET)
	public ResponseEntity<?> getAccessToken(){
		logger.debug("request access token");
		Map<String, String> tokenMap = new HashMap<String,String>();
		tokenMap.put("token", wechatServerService.getAccessToken());
		return ResponseEntity.ok(tokenMap);
	}
	
}
