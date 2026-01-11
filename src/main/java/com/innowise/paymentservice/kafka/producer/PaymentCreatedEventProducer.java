package com.innowise.paymentservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCreatedEventProducer {

    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    @Value("${kafka.topics.create-payment}")
    private String createPaymentTopic;

    public void send(PaymentCreatedEvent event) {
        kafkaTemplate.send(createPaymentTopic, event);
    }
}
