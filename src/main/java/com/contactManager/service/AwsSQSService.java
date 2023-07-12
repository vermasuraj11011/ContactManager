package com.contactManager.service;

import com.contactManager.config.AwsSQSConfig;
import com.contactManager.entities.EmailMessage;
import com.contactManager.payload.EmailDto;
import com.contactManager.payload.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class AwsSQSService {

    private final Logger LOGGER = LoggerFactory.getLogger(EmailMessage.class.getName());

    @Autowired
    private AwsSQSConfig awsSQSConfig;

    @Value("${cloud.aws.end-point.uri}")
    private String end_point;

    public void sendMessageToQueue(UserDto message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(message);

        this.awsSQSConfig
                .queueMessagingTemplate()
                .send(end_point, MessageBuilder.withPayload(jsonPayload).build());
    }

    public void sendMessageToQueue(EmailDto emailDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(emailDto) ;

        this.awsSQSConfig
                .queueMessagingTemplate()
                .send(end_point, MessageBuilder.withPayload(jsonPayload).build());
    }

    public void sendMessageToQueue(String  message) {
        this.awsSQSConfig
                .queueMessagingTemplate()
                .send(end_point, MessageBuilder.withPayload(message).build());
    }

//    Lister the queue

//    @SqsListener("email-queue")
//    public void loadMessageFromQueue(String message) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        UserDto userDto = objectMapper.readValue(message, UserDto.class);
//
//        LOGGER.info("messaged received {}", userDto);
//    }

//    @SqsListener("email-queue")
//    public void loadMessageFromQueue(String message){
//        LOGGER.info("messaged received {}", message);
//    }

}
