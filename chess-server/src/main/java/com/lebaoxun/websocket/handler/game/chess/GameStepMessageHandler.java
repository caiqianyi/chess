package com.lebaoxun.websocket.handler.game.chess;

import javax.annotation.Resource;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.lebaoxun.commons.exception.ResponseMessage;
import com.lebaoxun.websocket.entity.User;
import com.lebaoxun.websocket.handler.IMessageHandler;
import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;
import com.lebaoxun.websocket.server.Constants;
import com.lebaoxun.websocket.service.IRoomService;
import com.lebaoxun.websocket.service.IUserService;
import com.lebaoxun.websocket.service.IWebSessionMessageService;

@Service("msg_action_"+Constants.MSG_ACTION_30002)
public class GameStepMessageHandler implements IMessageHandler {

	@Resource
	private IUserService userService;
	
	@Resource
	private IRoomService roomService;
	
	@Resource
	private IWebSessionMessageService webSessionMessageService;
	
	@Override
	public SocketResponse doAction(Session session, SocketRequest request) {
		// TODO Auto-generated method stub
		String userId = request.getFrom();
		User user = userService.findById(userId);
		
		String to = roomService.getBroadcastTo(user.getRoomId());
		if(StringUtils.isNotBlank(to)){
			SocketResponse response = new SocketResponse();
			response.setFrom(userId);
			response.setMsgId(request.getMsgId());
			response.setTo(to);
			response.setResponse(ResponseMessage.ok(request.getParams()));
			return response;
		}
		return null;
	}

}