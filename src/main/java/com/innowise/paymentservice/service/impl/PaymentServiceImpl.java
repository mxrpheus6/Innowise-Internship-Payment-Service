package com.innowise.paymentservice.service.impl;

import com.innowise.paymentservice.dto.request.PaymentRequest;
import com.innowise.paymentservice.dto.response.PaymentResponse;
import com.innowise.paymentservice.dto.response.PaymentSumResponse;
import com.innowise.paymentservice.exception.custom.PaymentNotFoundException;
import com.innowise.paymentservice.kafka.producer.PaymentCreatedEvent;
import com.innowise.paymentservice.kafka.producer.PaymentCreatedEventProducer;
import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.mapper.PaymentSumMapper;
import com.innowise.paymentservice.model.Payment;
import com.innowise.paymentservice.model.enums.Status;
import com.innowise.paymentservice.repository.PaymentRepository;
import com.innowise.paymentservice.service.PaymentService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bson.types.Decimal128;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentSumMapper paymentSumMapper;

    private final PaymentCreatedEventProducer paymentCreatedEventProducer;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        Payment payment = paymentMapper.toEntity(request);

        int random = new Random().nextInt();
        payment.setStatus(random % 2 == 0 ? Status.SUCCESS : Status.FAILED);

        payment = paymentRepository.save(payment);

        PaymentCreatedEvent event = new PaymentCreatedEvent(
                UUID.fromString(payment.getOrderId()),
                payment.getStatus());
        paymentCreatedEventProducer.send(event);

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByStatuses(List<String> statuses) {
        return paymentRepository.findByStatusIn(statuses).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentSumResponse getTotalSumForPeriod(Instant start, Instant end) {
        Decimal128 decimal128 = paymentRepository.getTotalSumForPeriod(start, end);
        BigDecimal bigDecimal = decimal128.bigDecimalValue();
        return paymentSumMapper.toResponse(bigDecimal);
    }

}
