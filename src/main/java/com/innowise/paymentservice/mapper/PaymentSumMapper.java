package com.innowise.paymentservice.mapper;

import com.innowise.paymentservice.dto.response.PaymentSumResponse;
import java.math.BigDecimal;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(
        componentModel = ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PaymentSumMapper {
    PaymentSumResponse toResponse(BigDecimal total);
}
