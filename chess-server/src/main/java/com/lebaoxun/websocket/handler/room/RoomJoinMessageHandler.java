package com.lebaoxun.websocket.handler.room;

import javax.websocket.Session;

import org.springframework.stereotype.Service;

import com.lebaoxun.websocket.handler.IMessageHandler;
import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;
import com.lebaoxun.websocket.server.Constants;

@Service("msg_action_"+Constants.MSG_ACTION_20003)
public class RoomJoinMessageHandler implements IMessageHandler {

	@Override
	public SocketResponse doAction(Session session, SocketRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
