package com.nexstudio.nxsjms.listener;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;

import com.nexstudio.nxsjms.config.JmsConfig;
import com.nexstudio.nxsjms.model.HelloWorldMessage;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class HelloWorldListener {

    private final JmsTemplate jmsTemplate;
    
    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listener(
            @Payload HelloWorldMessage helloWorldMessage, 
            @Headers MessageHeaders messageHeaders, 
            Message message
    ){
        // System.out.println("Message Recieved");
        // System.out.println(helloWorldMessage);
    }

    @JmsListener(destination = JmsConfig.MY_HELLO_QUEUE)
    public void listenerForHello(
            @Payload HelloWorldMessage helloWorldMessage, 
            @Headers MessageHeaders messageHeaders, 
            Message message
    ) throws JMSException{

        HelloWorldMessage payLoadMsg = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("World!!")
                .build();

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), payLoadMsg);
    }
}
