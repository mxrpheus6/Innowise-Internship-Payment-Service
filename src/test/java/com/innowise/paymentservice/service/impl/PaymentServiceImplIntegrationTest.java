package com.innowise.paymentservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.innowise.paymentservice.client.RandomOrgClient;
import com.innowise.paymentservice.constants.TestConstants;
import com.innowise.paymentservice.dto.request.PaymentRequest;
import com.innowise.paymentservice.kafka.producer.PaymentCreatedEvent;
import com.innowise.paymentservice.model.Payment;
import com.innowise.paymentservice.model.enums.Status;
import com.innowise.paymentservice.repository.PaymentRepository;
import com.innowise.paymentservice.service.RandomNumberService;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class PaymentServiceImplIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Container
    static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private RandomNumberService randomNumberService;

    private KafkaConsumer<String, PaymentCreatedEvent> consumer;
    private BlockingQueue<ConsumerRecord<String, PaymentCreatedEvent>> records;

    @BeforeEach
    void setUp() {
        when(randomNumberService.getRandomInteger(0, 1)).thenReturn(0);

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.springframework.kafka.support.serializer.JsonDeserializer");
        consumerProps.put("spring.json.value.default.type", PaymentCreatedEvent.class.getName());
        consumerProps.put("spring.json.trusted.packages", "*");

        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singleton("CREATE_PAYMENT"));

        records = new LinkedBlockingQueue<>();
    }

    @AfterEach
    void cleanup() {
        paymentRepository.deleteAll();
        consumer.close();
    }

    @Test
    void givenPaymentRequest_whenCreatePayment_thenPersistToMongoAndSendKafkaEvent() throws InterruptedException {
        PaymentRequest request = PaymentRequest.builder()
                .orderId(TestConstants.ORDER_ID)
                .userId(TestConstants.USER_ID)
                .paymentAmount(TestConstants.PAYMENT_AMOUNT)
                .build();

        paymentService.createPayment(request);

        // --- Проверка MongoDB ---
        Payment saved = paymentRepository.findByOrderId(TestConstants.ORDER_ID).orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(TestConstants.USER_ID);
        assertThat(saved.getPaymentAmount()).isEqualTo(TestConstants.PAYMENT_AMOUNT);
        assertThat(saved.getStatus()).isEqualTo(Status.SUCCESS);

        // --- Проверка Kafka ---
        consumer.poll(Duration.ofSeconds(5)).forEach(records::offer);
        assertThat(records).isNotEmpty();

        ConsumerRecord<String, PaymentCreatedEvent> record = records.take();
        assertThat(record.value().getOrderId().toString()).isEqualTo(TestConstants.ORDER_ID);
        assertThat(record.value().getPaymentStatus()).isEqualTo(Status.SUCCESS);
    }
}
