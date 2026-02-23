package com.innowise.paymentservice.controller;

import com.innowise.paymentservice.dto.request.PaymentRequest;
import com.innowise.paymentservice.dto.response.PaymentResponse;
import com.innowise.paymentservice.dto.response.PaymentSumResponse;
import com.innowise.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody @Valid PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> getPaymentsByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatuses(@RequestParam List<String> statuses) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatuses(statuses));
    }

    @GetMapping("/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentSumResponse> getTotalSumForPeriod(
            @RequestParam Instant start,
            @RequestParam Instant end) {
        return ResponseEntity.ok(paymentService.getTotalSumForPeriod(start, end));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PaymentResponse>> getCurrentUserPayments(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) List<String> statuses) {

        String userId = jwt.getSubject();

        if (statuses != null) {
            return ResponseEntity.ok(
                    paymentService.getPaymentsByStatusesAndUserId(statuses, userId));
        }

        return ResponseEntity.ok(
                paymentService.getPaymentsByUserId(userId));
    }

    @GetMapping("/me/order/{orderId}")
    public ResponseEntity<PaymentResponse> getMyPaymentByOrderId(
            @PathVariable String orderId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();

        return ResponseEntity.ok(
                paymentService.getPaymentByOrderIdAndUserId(orderId, userId));
    }

    @GetMapping("/me/total")
    public ResponseEntity<PaymentSumResponse> getMyTotalSumForPeriod(
            @RequestParam Instant start,
            @RequestParam Instant end,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();

        return ResponseEntity.ok(
                paymentService.getTotalSumForPeriodAndUserId(userId, start, end));
    }

}
