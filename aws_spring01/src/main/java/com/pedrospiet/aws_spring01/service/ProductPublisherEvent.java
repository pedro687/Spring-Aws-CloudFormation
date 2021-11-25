package com.pedrospiet.aws_spring01.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrospiet.aws_spring01.enums.EventType;
import com.pedrospiet.aws_spring01.model.Envelope;
import com.pedrospiet.aws_spring01.model.Product;
import com.pedrospiet.aws_spring01.model.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductPublisherEvent {

    private AmazonSNS snsClient;
    private Topic topic;
    private ObjectMapper objectMapper;

    public ProductPublisherEvent(AmazonSNS snsClient, @Qualifier("productEventsTopic") Topic topic, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.topic = topic;
        this.objectMapper = objectMapper;
    }

    public void publishProductEvent(Product product, EventType eventType, String username) {
        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductId(product.getId());
        productEvent.setCode(product.getCode());
        productEvent.setUsername(username);

        Envelope envelope = new Envelope();
        envelope.setEventType(eventType);

        try {
            envelope.setData(objectMapper.writeValueAsString(productEvent));

            snsClient.publish(
              topic.getTopicArn(),
              objectMapper.writeValueAsString(envelope)
            );
        } catch (JsonProcessingException e) {
            log.error("Erro ao enviar produto");
        }


    }
}
