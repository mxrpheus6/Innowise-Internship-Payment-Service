package com.innowise.paymentservice.constants;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestConstants {
    public static final String USER_ID = "user123";
    public static final String WRONG_ORDER_ID = "wrongId";
    public static final String ORDER_ID = UUID.randomUUID().toString();

    public static final BigDecimal PAYMENT_AMOUNT = new BigDecimal("100.00");
    public static final BigDecimal TOTAL_SUM = new BigDecimal("250.50");
}
