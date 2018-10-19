package com.lebaoxun.websocket.protocol;

import java.io.Serializable;
import java.util.Map;

public class SocketRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3447738413037060880L;
	
	private String msgId;
	private String from;
	private Map<String, Object> params;
	
	public SocketRequest() {
		// TODO Auto-generated constructor stub
	}
	
	public SocketRequest(String msgId,
			String userId,Map<String, Object> params) {
		// TODO Auto-generated constructor stub
		this.msgId = msgId;
		this.params = params;
	}
	
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
}
