package com.innowise.paymentservice.kafka.consumer;

import com.innowise.paymentservice.dto.request.PaymentRequest;
import com.innowise.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "${kafka.topics.create-order}")
    public void consume(OrderCreatedEvent event) {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(event.getOrderId().toString())
                .userId(event.getUserId().toString())
                .paymentAmount(event.getTotal())
                .build();

        paymentService.createPayment(paymentRequest);
    }

}
