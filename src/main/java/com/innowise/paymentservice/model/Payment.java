package com.innowise.paymentservice.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "payments")
public class Payment {

    @MongoId
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String orderId;

    @Indexed
    private String userId;

    private String status;
    private OffsetDateTime timestamp;
    private BigDecimal paymentAmount;

}
