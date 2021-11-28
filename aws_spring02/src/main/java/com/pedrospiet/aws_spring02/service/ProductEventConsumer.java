package com.pedrospiet.aws_spring02.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrospiet.aws_spring02.model.Envelope;
import com.pedrospiet.aws_spring02.model.ProductEvent;
import com.pedrospiet.aws_spring02.model.SnsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOError;
import java.io.IOException;

@Service
@Slf4j
public class ProductEventConsumer {

    private ObjectMapper objectMapper;


    @Autowired
    public ProductEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProduct(TextMessage textMessage) throws IOException, JMSException {
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);

        Envelope envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);

        ProductEvent productEvent = objectMapper.readValue(envelope.getData(), ProductEvent.class);

        log.info("Receive message: {}", productEvent.getUsername());
    }

}
