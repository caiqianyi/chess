package com.lebaoxun.websocket.server;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
@RabbitListener(queues = Constants.EXECUTE_MESSAGE_QUQUE)
public class ExecuteMessageHandlerListener {

	private Logger logger = LoggerFactory.getLogger(ExecuteMessageHandlerListener.class);
	
	@Resource
	private IWebSessionMessageService webSessionMessageService;
	
	@Bean
    FanoutExchange fanoutExecuteMessage() {
        return new FanoutExchange(Constants.EXECUTE_MESSAGE);
    }
	
	@Bean
    public Queue queueExecuteMessageHandler() {
        return new Queue(Constants.EXECUTE_MESSAGE_QUQUE,true);
    }

    @Bean
    Binding bindingFanoutExchangeExecuteMessageHandler(Queue queueExecuteMessageHandler) {
        return BindingBuilder.bind(queueExecuteMessageHandler).to(fanoutExecuteMessage());
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
