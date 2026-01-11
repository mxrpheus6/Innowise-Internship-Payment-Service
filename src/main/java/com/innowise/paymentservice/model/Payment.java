package com.innowise.paymentservice.model;

import com.innowise.paymentservice.model.enums.Status;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @MongoId
    private String id = UUID.randomUUID().toString();

    @Indexed(name = "idx_payments_orderId_unique", unique = true)
    private String orderId;

    @Indexed(name = "idx_payments_userId")
    private String userId;

    @Indexed(name = "idx_payments_status")
    private Status status;

    @Indexed(name = "idx_payments_timestamp")
    private Instant timestamp;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal paymentAmount;

}
