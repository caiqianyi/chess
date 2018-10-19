package com.lebaoxun.websocket.server;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lebaoxun.websocket.protocol.SocketResponse;
import com.lebaoxun.websocket.service.IWebSessionMessageService;

/**
 * 房间广播消息
 * @author caiqianyi
 *
 */
@Component
@RabbitListener(queues = Constants.BROADCAST)
public class BroadcastListener {

	private Logger logger = LoggerFactory.getLogger(BroadcastListener.class);
	
	@Resource
	private IWebSessionMessageService webSessionMessageService;
	
	@Value("${server:port}")
	private String port;
	
	@Bean
    public Queue queueWebsocketBroadcast() {
        return new Queue(Constants.BROADCAST.replaceAll("#", port),true);
    }

    @Bean
    Binding bindingFanoutExchangeWebsocketBroadcast(Queue queueWebsocketBroadcast, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueWebsocketBroadcast).to(topicExchange).with(Constants.BROADCAST);
    }
	
	@RabbitHandler
    public void receive(Object data) {
		Message message = (Message) data;
		String text = new String(message.getBody());
		logger.debug("body={}",text);
		try{
			SocketResponse response = JSONObject.parseObject(text, SocketResponse.class);
			webSessionMessageService.broadcast(response);
		}catch(Exception e){
			e.printStackTrace();
		}
    }
}
