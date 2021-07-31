package com.nexstudio.nxsjms.sender;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexstudio.nxsjms.config.JmsConfig;
import com.nexstudio.nxsjms.model.HelloWorldMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 4000)
    public void sendMessage() {
        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Are we live yet?")
                .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
    }

    @Scheduled(fixedRate = 4000)
    public void sendAndReplyMessage() throws JMSException {

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello")
                .build();

        Message recievedMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_HELLO_QUEUE, new MessageCreator(){

            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMsg = null;

                try {
                    helloMsg = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMsg.setStringProperty("_type", "com.nexstudio.nxsjms.model.HelloWorldMessage");

                    System.out.println("Sending Hello");
    
                    return helloMsg;
                } catch (JsonProcessingException e) {
                    throw new JMSException("boom"); 
                }
            }
            
        });

        System.out.println(recievedMsg.getBody(String.class));
    }
}
