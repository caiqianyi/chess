package com.lebaoxun.websocket.service;

import com.lebaoxun.websocket.entity.Room;
import com.lebaoxun.websocket.entity.RoomMember;

public interface IRoomService {
	
	Room findById(String roomId);
	
	Room findFirst();
	
	RoomMember joinRoom(String roomId, String userId);
	
	RoomMember quitRoom(String userId);
	
	Room create(String userId, String gameId, String gameType);
	
	String getBroadcastTo(String roomId);
	
	String getFlag(String roomId);
	
	String restart(String roomId);
}
