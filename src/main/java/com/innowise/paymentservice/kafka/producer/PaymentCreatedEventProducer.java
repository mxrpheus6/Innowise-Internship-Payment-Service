package com.innowise.paymentservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCreatedEventProducer {
    private static final String TOPIC = "CREATE_PAYMENT";
    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    public void send(PaymentCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
