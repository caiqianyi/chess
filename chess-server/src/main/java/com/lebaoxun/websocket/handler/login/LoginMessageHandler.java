package com.lebaoxun.websocket.handler.login;

import javax.annotation.Resource;
import javax.websocket.Session;

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
import com.lebaoxun.websocket.service.impl.WebSessionMessageServiceImpl;

@Service("msg_action_"+Constants.MSG_ACTION_10002)
public class LoginMessageHandler implements IMessageHandler {

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
		user.setNickname((String)request.getParams().get("nicename"));
		
		userService.login(user, session);
		WebSessionMessageServiceImpl.send(session, new Gson().toJson(new SocketResponse(request,ResponseMessage.ok())));
		return null;
	}

}
