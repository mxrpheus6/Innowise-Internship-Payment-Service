package com.innowise.paymentservice.controller;

import com.innowise.paymentservice.dto.request.PaymentRequest;
import com.innowise.paymentservice.dto.response.PaymentResponse;
import com.innowise.paymentservice.dto.response.PaymentSumResponse;
import com.innowise.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(@RequestBody @Valid PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/orders/{orderId}")
    public PaymentResponse getPaymentsByOrderId(@PathVariable String orderId) {
        return paymentService.getPaymentByOrderId(orderId);
    }

    @GetMapping("/users/{userId}")
    public List<PaymentResponse> getPaymentsByUserId(@PathVariable String userId) {
        return paymentService.getPaymentsByUserId(userId);
    }

    @GetMapping
    public List<PaymentResponse> getPaymentsByStatuses(@RequestParam List<String> statuses) {
        return paymentService.getPaymentsByStatuses(statuses);
    }

    @GetMapping("/total")
    public PaymentSumResponse getTotalSumForPeriod(
            @RequestParam Instant start,
            @RequestParam Instant end) {
        return paymentService.getTotalSumForPeriod(start, end);
    }

}
