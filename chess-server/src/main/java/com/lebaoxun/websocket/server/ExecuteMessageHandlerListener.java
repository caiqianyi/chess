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
import com.lebaoxun.websocket.protocol.SocketRequest;
import com.lebaoxun.websocket.service.IWebSessionMessageService;

/**
 * 房间广播消息
 * @author caiqianyi
 *
 */
@Component
@RabbitListener(queues = Constants.EXECUTE_MESSAGE)
public class ExecuteMessageHandlerListener {

	private Logger logger = LoggerFactory.getLogger(ExecuteMessageHandlerListener.class);
	
	@Resource
	private IWebSessionMessageService webSessionMessageService;
	
	@Value("${server:port}")
	private String port;
	
	@Bean
    public Queue queueExecuteMessageHandler() {
        return new Queue(Constants.EXECUTE_MESSAGE,true);
    }

    @Bean
    Binding bindingFanoutExchangeExecuteMessageHandler(Queue queueExecuteMessageHandler, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueExecuteMessageHandler).to(topicExchange).with(Constants.EXECUTE_MESSAGE);
    }
	
	@RabbitHandler
    public void receive(Object data) {
		Message message = (Message) data;
		String text = new String(message.getBody());
		logger.debug("body={}",text);
		try{
			SocketRequest request = JSONObject.parseObject(text, SocketRequest.class);
			webSessionMessageService.doAction(request);
		}catch(Exception e){
			e.printStackTrace();
		}
    }
}
