package com.lebaoxun.websocket.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Room implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3948877924291115515L;
	private String id;
	private String gameId;
	private String gameType;
	private String createBy;
	private Date createTime;
	private Date endTime;
	private String logs;
	private List<RoomMember> members;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getLogs() {
		return logs;
	}
	public void setLogs(String logs) {
		this.logs = logs;
	}
	public List<RoomMember> getMembers() {
		return members;
	}
	public void setMembers(List<RoomMember> members) {
		this.members = members;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getGameType() {
		return gameType;
	}
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
