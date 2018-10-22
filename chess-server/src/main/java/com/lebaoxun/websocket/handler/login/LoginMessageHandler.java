package com.lebaoxun.websocket.handler.login;

import javax.annotation.Resource;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.lebaoxun.commons.exception.ResponseMessage;
import com.lebaoxun.websocket.entity.User;
import com.lebaoxun.websocket.handler.IMessageHandler;
import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;
import com.lebaoxun.websocket.server.Constants;
import com.lebaoxun.websocket.service.IUserService;
import com.lebaoxun.websocket.service.IWebSessionMessageService;

@Service("msg_action_"+Constants.MSG_ACTION_10002)
public class LoginMessageHandler implements IMessageHandler {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private IWebSessionMessageService webSessionMessageService;
	
	@Resource
	private IUserService userService;
	
	@Override
	public SocketResponse doAction(Session session, SocketRequest request) {
		// TODO Auto-generated method stub
		String from = request.getFrom();
		User user = new User();
		user.setUserId(from);
		user.setNickname((String)request.getParams().get("nickname"));
		logger.debug("login|user={}",new Gson().toJson(user));
		User u = userService.login(user, session);
		return new SocketResponse(request,ResponseMessage.ok(u));
	}

}
