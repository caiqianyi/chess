package com.lebaoxun.websocket.handler;

import javax.websocket.Session;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.lebaoxun.commons.exception.ResponseMessage;
import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;
import com.lebaoxun.websocket.server.Constants;
import com.lebaoxun.websocket.service.impl.WebSessionMessageServiceImpl;

@Service("msg_action_"+Constants.MSG_ACTION_10001)
public class HeartbeatMessageHandler implements IMessageHandler {

	@Override
	public SocketResponse doAction(Session session, SocketRequest request) {
		// TODO Auto-generated method stub
		WebSessionMessageServiceImpl.send(session, new Gson().toJson(new SocketResponse(request,ResponseMessage.ok())));
		return null;
	}

}
