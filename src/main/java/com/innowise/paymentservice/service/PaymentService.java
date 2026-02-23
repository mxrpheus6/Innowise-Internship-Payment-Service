package com.innowise.paymentservice.service;

import com.innowise.paymentservice.dto.request.PaymentRequest;
import com.innowise.paymentservice.dto.response.PaymentResponse;
import com.innowise.paymentservice.dto.response.PaymentSumResponse;
import java.time.Instant;
import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    PaymentResponse getPaymentByOrderId(String orderId);
    List<PaymentResponse> getPaymentsByUserId(String userId);
    List<PaymentResponse> getPaymentsByStatuses(List<String> statuses);
    PaymentSumResponse getTotalSumForPeriod(Instant start, Instant end);
    PaymentResponse getPaymentByOrderIdAndUserId(String orderId, String userId);
    List<PaymentResponse> getPaymentsByStatusesAndUserId(List<String> statuses, String userId);
    PaymentSumResponse getTotalSumForPeriodAndUserId(String userId, Instant start, Instant end);
}
