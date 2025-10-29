package com.innowise.paymentservice.kafka.consumer;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    private UUID orderId;
    private UUID userId;
    private BigDecimal total;
}
