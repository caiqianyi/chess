package com.lebaoxun.websocket.service;

import javax.websocket.Session;

import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.protocol.SocketResponse;

public interface IWebSessionMessageService {
	
	void put(String userId, Session session);
	
	boolean remove(String userId);
	
	Session get(String userId);
	
	SocketResponse doAction(SocketRequest request);

	void broadcast(SocketResponse response);
	
	void broadcastAll(SocketResponse response);
	
}
