package com.lebaoxun.websocket.handler;

import javax.websocket.Session;

import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;


public interface IMessageHandler {
	
	SocketResponse doAction(Session session, SocketRequest request);
}
