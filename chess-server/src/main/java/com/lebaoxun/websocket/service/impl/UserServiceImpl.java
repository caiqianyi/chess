package com.lebaoxun.websocket.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.lebaoxun.commons.exception.I18nMessageException;
import com.lebaoxun.soa.core.redis.IRedisHash;
import com.lebaoxun.websocket.entity.User;
import com.lebaoxun.websocket.service.IUserService;
import com.lebaoxun.websocket.service.IWebSessionMessageService;

@Service
public class UserServiceImpl implements IUserService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private IRedisHash redisHash;
	
	@Resource
	private IWebSessionMessageService webSessionMessageService;
	
	String USER_CACHE_KEY = "user:list";

	@Override
	public User login(User user,Session session) {
		// TODO Auto-generated method stub
		if(user == null || StringUtils.isBlank(user.getUserId())){
			throw new I18nMessageException("500");
		}
		if(redisHash.hExists(USER_CACHE_KEY, user.getUserId())){
			User u = (User) redisHash.hGet(USER_CACHE_KEY, user.getUserId());
			
			if(StringUtils.isBlank(u.getNickname())){
				u.setNickname(user.getNickname());
			}
			user = u ;
			logger.debug("user={}",new Gson().toJson(u));
		}else{
			//注冊
			user.setCreateTime(new Date());
		}
		user.setLoginTime(new Date());
		user.setOnline(true);
		redisHash.hSet(USER_CACHE_KEY, user.getUserId(), user);
		return user;
	}

	@Override
	public boolean logout(String userId) {
		// TODO Auto-generated method stub
		if( StringUtils.isBlank(userId)){
			throw new I18nMessageException("500");
		}
		Session session = webSessionMessageService.get(userId);
		if(session != null){
			webSessionMessageService.remove(userId);
			logger.debug("userId={}",userId);
			User user = (User) redisHash.hGet(USER_CACHE_KEY, userId);
			if(user != null && user.isOnline()){
				user.setLogoutTime(new Date());
				user.setOnline(false);
				redisHash.hSet(USER_CACHE_KEY, user.getUserId(), user);
			}
			return true;
		}
		return false;
	}

	@Override
	public User findById(String userId) {
		// TODO Auto-generated method stub
		return (User) redisHash.hGet(USER_CACHE_KEY, userId);
	}
	
	@Override
	public User update(User user) {
		// TODO Auto-generated method stub
		if(redisHash.hExists(USER_CACHE_KEY, user.getUserId())){
			User u = (User) redisHash.hGet(USER_CACHE_KEY, user.getUserId());
			
			if(user.getRoomId() != null){
				u.setRoomId(user.getRoomId());
			}
			logger.debug("user={}",new Gson().toJson(u));
		}
		return null;
	}

}
