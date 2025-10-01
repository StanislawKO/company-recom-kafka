package com.stas_kozh.recommendationservice.controller;

import com.stas_kozh.core.RecommendationCreatedWithCompanyEvent;
import com.stas_kozh.core.RecommendationResponseDto;
import com.stas_kozh.recommendationservice.exception.ErrorMessage;
import com.stas_kozh.recommendationservice.service.RecommendationService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static final String TOPIC = "recommendation-created-events-topic";

    private final KafkaTemplate<String, RecommendationResponseDto> kafkaTemplate;
    private final KafkaTemplate<String, RecommendationCreatedWithCompanyEvent> kafkaTemplate2;

    private final RecommendationService recommendationService;

    public KafkaController(
            KafkaTemplate<String, RecommendationResponseDto> kafkaTemplate,
            KafkaTemplate<String, RecommendationCreatedWithCompanyEvent> kafkaTemplate2,
            RecommendationService recommendationService) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplate2 = kafkaTemplate2;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/send")
    public ResponseEntity<Object> getAllRecommendation(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "25") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<RecommendationResponseDto> allRecommendations =
                recommendationService.getAllRecommendation(pageable).getContent();

        LOGGER.info("allRecommendations.get(0)).get(): {}", allRecommendations.get(0));
        SendResult<String, RecommendationResponseDto> result = null;
        try {
            result = kafkaTemplate
                    .send(TOPIC,
                            "getAllRecommendation",
                            allRecommendations.get(0)).get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(new Date(), e.getMessage()));
        }

        LOGGER.info("Topic getAllRecommendation: {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition getAllRecommendation: {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset getAllRecommendation: {}", result.getRecordMetadata().offset());

        LOGGER.info("Return getAllRecommendation: {}", allRecommendations);

        return ResponseEntity.status(HttpStatus.OK).body("getAllRecommendation");
    }

    @GetMapping("/send/{id}")
    public ResponseEntity<Object> getRecommendationById(@PathVariable Long id) {
        var recommendation = recommendationService.getRecommendationById(id);
        String recommendationId = recommendation.getId().toString();

        ProducerRecord<String, RecommendationResponseDto> record = new ProducerRecord<>(
                TOPIC,
                recommendationId,
                recommendation
        );

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, RecommendationResponseDto> result = null;
        try {
            result = kafkaTemplate
                    .send(record).get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(new Date(), e.getMessage()));
        }

        LOGGER.info("Topic getRecommendationById: {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition getRecommendationById: {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset getRecommendationById: {}", result.getRecordMetadata().offset());

        LOGGER.info("Return getRecommendationById: {}", recommendation);

        return ResponseEntity.status(HttpStatus.OK).body(recommendationId);
    }

    @PostMapping("/send/company")
    public ResponseEntity<Object> createRecommendationWithCompany(
            @RequestBody RecommendationCreatedWithCompanyEvent recommendationCreatedWithCompanyEvent
    ) {
        ProducerRecord<String, RecommendationCreatedWithCompanyEvent> record = new ProducerRecord<>(
                "recommendation-created-with-company-events-topic",
                UUID.randomUUID().toString(),
                recommendationCreatedWithCompanyEvent
        );

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, RecommendationCreatedWithCompanyEvent> result = null;
        try {
            result = kafkaTemplate2
                    .send(record).get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(new Date(), e.getMessage()));
        }

        LOGGER.info("Topic createRecommendationWithCompany: {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition createRecommendationWithCompany: {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset createRecommendationWithCompany: {}", result.getRecordMetadata().offset());

        LOGGER.info("Return getFirstCapital createRecommendationWithCompany: {}",
                recommendationCreatedWithCompanyEvent.getFirstCapital());

        return ResponseEntity.status(HttpStatus.CREATED).body(recommendationCreatedWithCompanyEvent.getCompanyName());
    }

    @PostMapping("/send")
    public ResponseEntity<Object> createRecommendation(
            @RequestBody RecommendationResponseDto recommendationResponseDto
    ) {
        var recommendation = recommendationService.createRecommendation(recommendationResponseDto);
        String recommendationId = recommendation.getId().toString();

        CompletableFuture<SendResult<String, RecommendationResponseDto>> future = kafkaTemplate
                .send(TOPIC, recommendationId, recommendationResponseDto);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send message: {}", exception.getMessage());
            } else {
                LOGGER.info("Message sent successfully: {}", result.getRecordMetadata());
                LOGGER.info("Topic createRecommendation: {}", result.getRecordMetadata().topic());
                LOGGER.info("Partition createRecommendation: {}", result.getRecordMetadata().partition());
                LOGGER.info("Offset createRecommendation: {}", result.getRecordMetadata().offset());
            }

        });

        LOGGER.info("Return createRecommendation: {} {}", recommendationId, recommendation);

        return ResponseEntity.status(HttpStatus.CREATED).body(recommendationId);
    }

    @PutMapping("/send/{id}")
    public ResponseEntity<Object> updateRecommendation(
            @PathVariable Long id,
            @RequestBody RecommendationResponseDto recommendationResponseDto
    ) {
        var recommendation = recommendationService.getRecommendationById(id);

        recommendationService.updateRecommendation(id, recommendationResponseDto);
        recommendation = recommendationService.getRecommendationById(id);

        SendResult<String, RecommendationResponseDto> result = null;
        try {
            result = kafkaTemplate
                    .send(TOPIC, id.toString(), recommendation).get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(new ErrorMessage(new Date(), e.getMessage()));
        }

        LOGGER.info("Topic updateRecommendation: {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition updateRecommendation: {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset updateRecommendation: {}", result.getRecordMetadata().offset());

        LOGGER.info("Return updateRecommendation: {}", recommendation);

        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

}
