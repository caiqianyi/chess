package com.lebaoxun.websocket.service;

import javax.websocket.Session;

import com.lebaoxun.websocket.entity.User;

public interface IUserService {
	
	User login(User user,Session session);
	
	boolean logout(String userId);
	
	User findById(String userId);
	
	User update(User user);
}
