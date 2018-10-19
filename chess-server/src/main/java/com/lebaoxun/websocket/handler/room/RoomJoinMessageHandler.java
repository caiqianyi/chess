package com.lebaoxun.websocket.handler.room;

import javax.annotation.Resource;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.lebaoxun.commons.exception.ResponseMessage;
import com.lebaoxun.websocket.entity.RoomMember;
import com.lebaoxun.websocket.handler.IMessageHandler;
import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;
import com.lebaoxun.websocket.server.Constants;
import com.lebaoxun.websocket.service.IRoomService;

@Service("msg_action_"+Constants.MSG_ACTION_20003)
public class RoomJoinMessageHandler implements IMessageHandler {

	@Resource
	private IRoomService roomService;
	
	@Override
	public SocketResponse doAction(Session session, SocketRequest request) {
		// TODO Auto-generated method stub
		String userId = request.getFrom();
		String roomId = (String)request.getParams().get("roomId");
		RoomMember member = roomService.joinRoom(roomId, userId);
		
		String to = roomService.getBroadcastTo(roomId);
		if(StringUtils.isBlank(to)){
			SocketResponse response = new SocketResponse();
			response.setFrom(userId);
			response.setMsgId(request.getMsgId());
			response.setTo(to);
			response.setResponse(ResponseMessage.ok(member));
			return response;
		}
		return  null;
	}

}
