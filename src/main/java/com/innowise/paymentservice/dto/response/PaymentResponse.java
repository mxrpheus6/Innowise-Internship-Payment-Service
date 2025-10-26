package com.innowise.paymentservice.dto.response;

import com.innowise.paymentservice.model.enums.Status;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private String id;
    private String orderId;
    private String userId;
    private Status status;
    private Instant timestamp;
    private BigDecimal paymentAmount;
}
