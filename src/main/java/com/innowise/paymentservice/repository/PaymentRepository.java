package com.innowise.paymentservice.repository;

import com.innowise.paymentservice.model.Payment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByUserId(String userId);
    List<Payment> findByStatusIn(List<String> statuses);
    List<Payment> findByTimestampBetween(Instant start, Instant end);

    @Aggregation(pipeline = {
            "{ $match: { timestamp: { $gte: ?0, $lte: ?1 } } }",
            "{ $group: { _id: null, total: { $sum: '$paymentAmount' } } }"
    })
    Decimal128 getTotalSumForPeriod(Instant start, Instant end);
}
