package com.lebaoxun.websocket.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.lebaoxun.soa.core.redis.IRedisHash;
import com.lebaoxun.websocket.entity.Room;
import com.lebaoxun.websocket.entity.RoomMember;
import com.lebaoxun.websocket.entity.User;
import com.lebaoxun.websocket.service.IRoomService;
import com.lebaoxun.websocket.service.IUserService;

@Service
public class RoomServiceImpl implements IRoomService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private IRedisHash redisHash;
	
	@Resource(name="redisTemplate")
	private RedisTemplate<String, Object> redisTemplate2;

	@Resource
	private IUserService userService;
	
	String ROOM_CACHE_KEY = "room:list";
	
	@Override
	public Room findById(String roomId) {
		// TODO Auto-generated method stub
		logger.debug("roomId={}",roomId);
		if(roomId != null){
			Room room = (Room) redisHash.hGet(ROOM_CACHE_KEY, roomId);
			if(room != null){
				List<RoomMember> members = room.getMembers();
				for(RoomMember member : members){
					String userId = member.getUser().getUserId();
					User user = userService.findById(userId);
					if(user != null){
						member.setUser(user);
					}
				}
				return room;
			}
		}
		return null;
	}
	
	@Override
	public Room findFirst() {
		// TODO Auto-generated method stub
		Map<String,Object> all = redisHash.hGetAll(ROOM_CACHE_KEY);
		if(all != null && !all.isEmpty()){
			return findById(all.keySet().iterator().next()); 
		}
		return null;
	}
	
	private int findMember(List<RoomMember> members,String userId){
		for(int i=0;i<members.size();i++){
			RoomMember member = members.get(i);
			String uid = member.getUser().getUserId();
			if(uid.equals(userId)){
				return i;
			}
		}
		return -1;
	}
	private RoomMember findMemberByRole(List<RoomMember> members,String role){
		for(RoomMember member : members){
			String r = member.getRole();
			if(r.equals(role)){
				return member;
			}
		}
		return null;
	}
	
	private String getMemberRole(List<RoomMember> members){
		String roles[] = new String[]{"Admin","Player","OB"};
		if(findMemberByRole(members, roles[0]) == null){
			return roles[0];
		}
		if(findMemberByRole(members, roles[1]) == null){
			return roles[1];
		}
		return roles[2];
	}

	@Override
	public RoomMember joinRoom(String roomId, String userId) {
		// TODO Auto-generated method stub
		User user = userService.findById(userId);
		if(user != null){
			Room room = findById(roomId);
			if(room != null){
				List<RoomMember> members = room.getMembers();
				int i = findMember(members, userId);
				
				String role = getMemberRole(room.getMembers());
				RoomMember member = null;
				if(i > -1){
					member = members.get(i);
				}else{
					member = new RoomMember();
					member.setRole(role);
					member.setUser(user);
					members.add(member);
				}
				member.setFlag("0");
				member.setJoinDate(new Date());
				room.setMembers(members);
				user.setRoomId(roomId);
				userService.update(user);
				redisHash.hSet(ROOM_CACHE_KEY, roomId, room);
				return member;
				
			}
		}
		return null;
	}

	@Override
	public RoomMember quitRoom(String userId) {
		User user = userService.findById(userId);
		if(user != null){
			String roomId = user.getRoomId();
			Room room = findById(roomId);
			if(room != null){
				List<RoomMember> members = room.getMembers();
				int i = findMember(members, userId);
				if(i > -1){
					RoomMember member = members.get(i);
					user.setRoomId(null);
					userService.update(user);
					member.setFlag("-1");
					member.setQuitDate(new Date());
					room.setMembers(members);
					redisHash.hSet(ROOM_CACHE_KEY, roomId, room);
					return member;
				}
			}
		}
		return null;
	}

	@Override
	public Room create(String userId, String gameId, String gameType) {
		// TODO Auto-generated method stub
		User user = userService.findById(userId);
		if(user != null){
			String roomId = getRoomId(new Date());
			Room room = new Room();
			room.setCreateBy(userId);
			room.setCreateTime(new Date());
			room.setGameId(gameId);
			room.setGameType(gameType);
			room.setId(roomId);
			room.setFlag("0");
			
			RoomMember member = new RoomMember();
			member.setJoinDate(new Date());
			member.setRole("Admin");
			member.setUser(user);
			member.setFlag("0");
			List<RoomMember> members = new ArrayList<RoomMember>();
			members.add(member);
			room.setMembers(members);
			
			user.setRoomId(roomId);
			userService.update(user);
			redisHash.hSet(ROOM_CACHE_KEY, roomId, room);
			return room;
		}
		return null;
	}
	
	@Override
	public String getFlag(String roomId) {
		Room room = findById(roomId);
		if(room != null){
			if(room.getFlag() == null || "0".equals(room.getFlag())){
				List<RoomMember> members = room.getMembers();
				
				RoomMember mem1 = findMemberByRole(members, "Admin"),
						mem2 = findMemberByRole(members, "Player");
				
				if(mem1 != null && mem2 != null){
					room.setFlag("1");
					redisHash.hSet(ROOM_CACHE_KEY, roomId, room);
					return "3";
				}
			}
			return room.getFlag();
		}
		return "-1";
	}
	
	@Override
	public String restart(String roomId) {
		Room room = findById(roomId);
		if(room != null){
			List<RoomMember> members = room.getMembers();
			RoomMember mem1 = findMemberByRole(members, "Admin"),
					mem2 = findMemberByRole(members, "Player");
			if(mem1 != null && "0".equals(mem1.getFlag()) 
					&& mem2 != null && "0".equals(mem2.getFlag())){
				room.setFlag("1");
				redisHash.hSet(ROOM_CACHE_KEY, roomId, room);
				return "1";
			}
			return "0";
		}
		return "-1";
	}

	private synchronized String getRoomId(Date now) {
		String date = DateFormatUtils.format(now, "yyyyMMdd");
		String key = "room:id:" + date;
		Integer inr = (Integer) redisTemplate2.opsForValue().get(key);
		if (inr == null) {
			inr = RandomUtils.nextInt(10000);
			redisTemplate2.opsForValue().set(key, inr.intValue(), 25l,
					TimeUnit.valueOf("HOURS"));
		} else {
			inr = redisTemplate2.opsForValue().increment(key, 1).intValue();
		}
		String orderNo = "ROOM" + date + format1(inr, 5);
		return orderNo;
	}
	
	public static String format1(Integer value, int minLength) {
		StringBuffer st = new StringBuffer(value.toString());
		if (st.length() < minLength) {
			int len = minLength - st.length();
			for (int i = 0; i < len; i++) {
				st.insert(0, "0");
			}
		}
		return st.toString();
	}
	
	@Override
	public String getBroadcastTo(String roomId) {
		// TODO Auto-generated method stub
		Room room = findById(roomId);
		if(room != null && room.getMembers() != null && !room.getMembers().isEmpty()){
			StringBuffer tos = new StringBuffer();
			for(RoomMember member : room.getMembers()){
				tos.append(member.getUser().getUserId()+";");
			}
			tos.delete(tos.length()-1, tos.length());
			return tos.toString();
		}
		return null;
	}
}
