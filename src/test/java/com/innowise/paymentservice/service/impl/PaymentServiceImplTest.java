package com.innowise.paymentservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.paymentservice.client.RandomOrgClient;
import com.innowise.paymentservice.constants.TestConstants;
import com.innowise.paymentservice.dto.request.PaymentRequest;
import com.innowise.paymentservice.dto.response.PaymentResponse;
import com.innowise.paymentservice.dto.response.PaymentSumResponse;
import com.innowise.paymentservice.exception.custom.PaymentNotFoundException;
import com.innowise.paymentservice.kafka.producer.PaymentCreatedEvent;
import com.innowise.paymentservice.kafka.producer.PaymentCreatedEventProducer;
import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.mapper.PaymentSumMapper;
import com.innowise.paymentservice.model.Payment;
import com.innowise.paymentservice.repository.PaymentRepository;
import com.innowise.paymentservice.service.RandomNumberService;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentSumMapper paymentSumMapper;

    @Mock
    private PaymentCreatedEventProducer paymentCreatedEventProducer;

    @Mock
    private RandomNumberService randomNumberService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();

        payment = new Payment();
        payment.setOrderId(TestConstants.ORDER_ID);
        payment.setUserId(TestConstants.USER_ID);
        payment.setPaymentAmount(TestConstants.PAYMENT_AMOUNT);

        paymentResponse = new PaymentResponse();
        paymentResponse.setOrderId(TestConstants.ORDER_ID);
        paymentResponse.setUserId(TestConstants.USER_ID);
    }

    @Test
    void givenValidRequest_whenCreatePayment_thenReturnPaymentResponse() {
        when(paymentMapper.toEntity(paymentRequest)).thenReturn(payment);
        when(randomNumberService.getRandomInteger(0, 1)).thenReturn(0);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.createPayment(paymentRequest);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(TestConstants.ORDER_ID);
        verify(paymentMapper).toEntity(paymentRequest);
        verify(paymentRepository).save(payment);
        verify(paymentCreatedEventProducer).send(any(PaymentCreatedEvent.class));
    }

    @Test
    void givenOrderId_whenGetPaymentByOrderId_thenReturnPaymentResponse() {
        when(paymentRepository.findByOrderId(TestConstants.ORDER_ID)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.getPaymentByOrderId(TestConstants.ORDER_ID);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(TestConstants.ORDER_ID);
    }

    @Test
    void givenInvalidOrderId_whenGetPaymentByOrderId_thenThrowException() {
        when(paymentRepository.findByOrderId(TestConstants.WRONG_ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentByOrderId(TestConstants.WRONG_ORDER_ID))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessage("Payment not found");
    }

    @Test
    void givenUserId_whenGetPaymentsByUserId_thenReturnListOfResponses() {
        when(paymentRepository.findByUserId(TestConstants.USER_ID)).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        List<PaymentResponse> result = paymentService.getPaymentsByUserId(TestConstants.USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOrderId()).isEqualTo(TestConstants.ORDER_ID);
    }

    @Test
    void givenStatuses_whenGetPaymentsByStatuses_thenReturnFilteredPayments() {
        List<String> statuses = List.of("SUCCESS", "FAILED");
        when(paymentRepository.findByStatusIn(statuses)).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        List<PaymentResponse> result = paymentService.getPaymentsByStatuses(statuses);

        assertThat(result).hasSize(1);
        verify(paymentRepository).findByStatusIn(statuses);
    }

    @Test
    void givenDateRange_whenGetTotalSumForPeriod_thenReturnSumResponse() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        Decimal128 decimal128 = new Decimal128(TestConstants.TOTAL_SUM);
        PaymentSumResponse sumResponse = new PaymentSumResponse(TestConstants.TOTAL_SUM);

        when(paymentRepository.getTotalSumForPeriod(start, end)).thenReturn(decimal128);
        when(paymentSumMapper.toResponse(decimal128.bigDecimalValue())).thenReturn(sumResponse);

        PaymentSumResponse result = paymentService.getTotalSumForPeriod(start, end);

        assertThat(result.getTotal()).isEqualTo(TestConstants.TOTAL_SUM);
        verify(paymentRepository).getTotalSumForPeriod(start, end);
    }
}
