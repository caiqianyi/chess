package com.lebaoxun.websocket.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.lebaoxun.commons.beans.BeanFactoryUtils;
import com.lebaoxun.websocket.handler.IMessageHandler;
import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;
import com.lebaoxun.websocket.server.Constants;
import com.lebaoxun.websocket.service.IWebSessionMessageService;

@Service
public class WebSessionMessageServiceImpl implements IWebSessionMessageService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static Map<String, Session> sessionMap = new HashMap<String, Session>();
	
	@Override
	public void put(String userId, Session session) {
		sessionMap.put(userId, session);
	}

	@Override
	public boolean remove(String userId) {
		sessionMap.remove(userId);
		return !sessionMap.containsKey(userId);
	}

	@Override
	public synchronized void broadcast(SocketResponse response) {
		// TODO Auto-generated method stub
		if(StringUtils.isNotBlank(response.getTo())){
			String tos[] = response.getTo().split(";");
			for (String userId : tos) {
				Session session = sessionMap.get(userId);
				if(session != null){
					send(session, new Gson().toJson(response));
				}
			}
		}else{
			for (String userId : sessionMap.keySet()) {
				Session session = sessionMap.get(userId);
				if(session != null){
					send(session, new Gson().toJson(response));
				}
			}
		}
	}
	
	@Override
	public Session get(String userId) {
		// TODO Auto-generated method stub
		return sessionMap.get(userId);
	}
	
	@Override
	public SocketResponse doAction(SocketRequest request) {
		if(request != null){
			IMessageHandler handlerAction = (IMessageHandler) BeanFactoryUtils.getBean("msg_action_"+request.getMsgId());
			if(handlerAction == null){
				logger.error("errcode={},errmsg={},mp={}","404","未发现消息‘"+request.getMsgId()+"’",new Gson().equals(request));
				return null;
				//throw new I18nMessageException("404","未发现消息‘"+mp.getMsgId()+"’");
			}
			Session session = sessionMap.get(request.getFrom());
			if(session != null){
				SocketResponse response = handlerAction.doAction(session, request);
				if(response != null){
					String message = new Gson().toJson(response);
					logger.debug("send|message={}",message);
					RabbitTemplate rabbitTemplate = (RabbitTemplate) BeanFactoryUtils.getBean(RabbitTemplate.class);
					rabbitTemplate.convertAndSend(Constants.BROADCAST, Constants.BROADCAST_QUQUE, message);
				}
			}
		}
		return null;
	}
	
	public static void send(Session session,String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
