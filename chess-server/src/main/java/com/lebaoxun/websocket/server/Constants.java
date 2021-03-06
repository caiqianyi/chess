package com.lebaoxun.websocket.server;

public class Constants {
	
	public static final String SERVER = "8082";
	
	public static final String BROADCAST = "websocket.broadcast";
	public static final String BROADCAST_QUQUE = "websocket.broadcast."+SERVER;
	public static final String EXECUTE_MESSAGE = "websocket.message.execute";
	public static final String EXECUTE_MESSAGE_QUQUE = "websocket.message.executet."+SERVER;
	
	
	public static final String MSG_ACTION_10001 = "10001";//心跳
	public static final String MSG_ACTION_10002 = "10002";//登录
	public static final String MSG_ACTION_10003 = "10003";//注销
	
	public static final String MSG_ACTION_20001 = "20001";//创建房间
	public static final String MSG_ACTION_20002 = "20002";//房间信息，成员
	public static final String MSG_ACTION_20003 = "20003";//加入房间
	public static final String MSG_ACTION_20004 = "20004";//退出房间
	
	
	public static final String MSG_ACTION_30001 = "30001";//游戏棋
	public static final String MSG_ACTION_30002 = "30002";//游戏棋
	
}