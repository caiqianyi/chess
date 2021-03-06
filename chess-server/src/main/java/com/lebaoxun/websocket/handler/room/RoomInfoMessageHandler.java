package com.lebaoxun.websocket.handler.room;

import javax.annotation.Resource;
import javax.websocket.Session;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.lebaoxun.commons.exception.ResponseMessage;
import com.lebaoxun.websocket.handler.IMessageHandler;
import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;
import com.lebaoxun.websocket.server.Constants;
import com.lebaoxun.websocket.service.IRoomService;
import com.lebaoxun.websocket.service.impl.WebSessionMessageServiceImpl;

@Service("msg_action_"+Constants.MSG_ACTION_20002)
public class RoomInfoMessageHandler implements IMessageHandler {
	
	@Resource
	private IRoomService roomService;
	
	@Override
	public SocketResponse doAction(Session session, SocketRequest request) {
		// TODO Auto-generated method stub
		WebSessionMessageServiceImpl.send(session, new Gson().toJson(new SocketResponse(request,ResponseMessage.ok(roomService.findFirst()))));
		return null;
	}

}
