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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lebaoxun.commons.beans.BeanFactoryUtils;
import com.lebaoxun.commons.exception.I18nMessageException;
import com.lebaoxun.commons.exception.ResponseMessage;
import com.lebaoxun.commons.utils.StringUtils;
import com.lebaoxun.soa.amqp.core.sender.IRabbitmqSender;
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
			
			SocketResponse response = new SocketResponse();
			response.setFrom(userId);
			response.setMsgId("200");
			response.setResponse(ResponseMessage.ok());
			WebSessionMessageServiceImpl.send(session, new Gson().toJson(response));
		} catch (I18nMessageException e) {
			e.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(Session session, String body) {
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
					String message = new Gson().toJson(response);
					logger.debug("send|message={}", message);
					Environment env = BeanFactoryUtils
							.getBean(Environment.class);
					String port = env.getProperty("server.port");
					IRabbitmqSender rabbitmqSender = (IRabbitmqSender) BeanFactoryUtils
							.getBean("baseAmqpSender");
					logger.debug("send|port={},rabbitmqSender={}", port,
							rabbitmqSender);
					rabbitmqSender.sendContractFanout(
							Constants.BROADCAST.replaceAll("#", port), message);
				}
			}
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
    public void onClose(Session session){
		IUserService userService = BeanFactoryUtils
				.getBean(IUserService.class);
		userService.logout(userId);
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
