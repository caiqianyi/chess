package com.lebaoxun.websocket.entity;

import java.io.Serializable;
import java.util.Date;

public class RoomMember implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5277327239888757624L;
	private String role;
	private Date joinDate;
	private Date quitDate;
	private String flag;
	private User user;
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	public Date getQuitDate() {
		return quitDate;
	}
	public void setQuitDate(Date quitDate) {
		this.quitDate = quitDate;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
}
