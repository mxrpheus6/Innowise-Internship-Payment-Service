package com.innowise.paymentservice.dto.request;

import com.innowise.paymentservice.model.enums.Status;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
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
public class PaymentRequest {

    @NotBlank(message = "{payment_request.order_id.blank}")
    private String orderId;

    @NotBlank(message = "{payment_request.user_id.blank}")
    private String userId;

    @NotNull(message = "{payment_request.amount.null}")
    @Digits(integer = 10, fraction = 2, message = "{payment_request.amount.digits}")
    @PositiveOrZero(message = "{payment_request.amount.positive_or_zero}")
    private BigDecimal paymentAmount;

}
