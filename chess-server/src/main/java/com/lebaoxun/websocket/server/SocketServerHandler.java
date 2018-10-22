package com.lebaoxun.websocket.server;

import java.util.Map;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lebaoxun.commons.beans.BeanFactoryUtils;
import com.lebaoxun.commons.exception.I18nMessageException;
import com.lebaoxun.commons.exception.ResponseMessage;
import com.lebaoxun.commons.utils.StringUtils;
import com.lebaoxun.websocket.handler.IMessageHandler;
import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;
import com.lebaoxun.websocket.service.IUserService;
import com.lebaoxun.websocket.service.IWebSessionMessageService;
import com.lebaoxun.websocket.service.impl.WebSessionMessageServiceImpl;

@ServerEndpoint(value = "/socket/server/{userId}")
@Component
public class SocketServerHandler {

	private Logger logger = LoggerFactory.getLogger(SocketServerHandler.class);
	
	private String userId;

	/**
	 * 连接建立成功调用的方法
	 * 
	 * @param session
	 *            可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(@PathParam("userId") String userId, Session session,
			EndpointConfig config) {
		try {
			this.userId = userId;
			Map<String, Object> userProperties = session.getUserProperties();
			logger.info("session userProperties:{} ", userProperties);

			IWebSessionMessageService webSessionMessageService = BeanFactoryUtils
					.getBean(IWebSessionMessageService.class);
			webSessionMessageService.put(userId, session);
			
			IUserService userService = BeanFactoryUtils
					.getBean(IUserService.class);
			SocketResponse response = new SocketResponse();
			response.setFrom(userId);
			response.setMsgId("200");
			response.setResponse(ResponseMessage.ok(userService.findById(userId)));
			
			WebSessionMessageServiceImpl.send(session, new Gson().toJson(response));
		} catch (I18nMessageException e) {
			e.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(Session session, String body) {
		try{
			if (StringUtils.isNotBlank(body)) {
				SocketRequest request = JSONObject.parseObject(body,
						SocketRequest.class);
				if (request != null) {
					if (!"10001".equals(request.getMsgId())) {// 不是心跳
						logger.debug("body={}", body);
					}
					IMessageHandler handlerAction = (IMessageHandler) BeanFactoryUtils
							.getBean("msg_action_" + request.getMsgId());
					if (handlerAction == null) {
						logger.error("errcode={},errmsg={},mp={}", "404", "未发现消息‘"
								+ request.getMsgId() + "’",
								new Gson().equals(request));
						return;
						// throw new
						// I18nMessageException("404","未发现消息‘"+mp.getMsgId()+"’");
					}
					SocketResponse response = handlerAction.doAction(session,
							request);
					if (response != null) {
						
						IWebSessionMessageService webSessionMessageService = BeanFactoryUtils
								.getBean(IWebSessionMessageService.class);
						webSessionMessageService.broadcastAll(response);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			SocketResponse response = new SocketResponse();
			response.setFrom(userId);
			response.setMsgId("500");
			response.setResponse(ResponseMessage.error("500", "系统异常"));
			WebSessionMessageServiceImpl.send(session, new Gson().toJson(response));
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
    public void onClose(Session session){
		try{
			IMessageHandler handlerAction10003 = (IMessageHandler) BeanFactoryUtils
					.getBean("msg_action_" + "10003");
			IWebSessionMessageService webSessionMessageService = BeanFactoryUtils
					.getBean(IWebSessionMessageService.class);
			if (handlerAction10003 != null) {
				SocketRequest request = new SocketRequest();
				request.setFrom(userId);
				request.setMsgId("10003");
				SocketResponse response = handlerAction10003.doAction(session,
						request);
				if (response != null) {
					webSessionMessageService.broadcastAll(response);
				}
			}
			IMessageHandler handlerAction20004 = (IMessageHandler) BeanFactoryUtils
					.getBean("msg_action_" + "20004");
			if (handlerAction20004 != null) {
				SocketRequest request = new SocketRequest();
				request.setFrom(userId);
				request.setMsgId("20004");
				SocketResponse response = handlerAction20004.doAction(session,
						request);
				if (response != null) {
					webSessionMessageService.broadcastAll(response);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
    }

	/**
	 * 发生错误时调用
	 * 
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

}
