package com.lebaoxun.websocket.protocol;

import java.io.Serializable;

import com.lebaoxun.commons.exception.ResponseMessage;

public class SocketResponse  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7598331748873880303L;
	
	private String msgId;
	private String from;
	private String to;
	private ResponseMessage response;
	
	public SocketResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public SocketResponse(SocketRequest request,ResponseMessage response) {
		// TODO Auto-generated constructor stub
		this.msgId = request.getMsgId();
		this.from = request.getFrom();
		this.response = response;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public ResponseMessage getResponse() {
		return response;
	}

	public void setResponse(ResponseMessage response) {
		this.response = response;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
