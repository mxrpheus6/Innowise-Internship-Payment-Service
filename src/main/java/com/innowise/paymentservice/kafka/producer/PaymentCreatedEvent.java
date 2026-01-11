package com.innowise.paymentservice.kafka.producer;

import com.innowise.paymentservice.model.enums.Status;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreatedEvent {
    private UUID orderId;
    private Status paymentStatus;
}
