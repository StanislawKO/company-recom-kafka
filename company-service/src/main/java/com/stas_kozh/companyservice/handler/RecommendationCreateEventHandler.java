package com.stas_kozh.companyservice.handler;

import com.stas_kozh.companyservice.dto.CompanyMapper;
import com.stas_kozh.companyservice.exception.NonRetryableException;
import com.stas_kozh.companyservice.exception.RetryableException;
import com.stas_kozh.companyservice.model.Company;
import com.stas_kozh.companyservice.model.CompanyUpdatedEvent;
import com.stas_kozh.companyservice.serivce.CompanyService;
import com.stas_kozh.core.CompanyResponseDto;
import com.stas_kozh.core.RecommendationWithCompanyResponseDto;
import com.stas_kozh.core.RecommendationCreatedWithCompanyEvent;
import com.stas_kozh.core.RecommendationResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics = {"recommendation-created-events-topic", "recommendation-created-with-company-events-topic"})
@RequiredArgsConstructor
public class RecommendationCreateEventHandler {

    private final String url = "http://localhost:9090/response/200";

    private final CompanyService service;

    private final KafkaTemplate<String, CompanyResponseDto> kafkaTemplate;
    private final KafkaTemplate<String, RecommendationWithCompanyResponseDto> kafkaTemplate2;

    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static final String TOPIC = "company-created-events-topic";

    private final CompanyMapper companyMapper;

    @KafkaHandler
    public void handle(RecommendationResponseDto recommendationDto) {
        LOGGER.info("Received event RecommendationResponseDto: {}", recommendationDto.getRecommendation());

        createAnswerBack(recommendationDto);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response RecommendationResponseDto: {}", response.getBody());
            }
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage());
            throw new RetryableException(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }

    @Transactional
    @KafkaHandler
    public void handle(@Payload RecommendationCreatedWithCompanyEvent recommendationCreatedWithCompanyEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        LOGGER.info("Received event RecommendationCreatedWithCompanyEvent: {}",
                recommendationCreatedWithCompanyEvent.getRecommendation());

        CompanyResponseDto companyResponseDto = service.getCompanyByMessageId(messageId);

        if (companyResponseDto != null) {
            LOGGER.info("Duplicate massage id: {}", messageId);
            return;
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response RecommendationCreatedWithCompanyEvent: {}", response.getBody());
            }
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage());
            throw new RetryableException(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }

        try {
            Company company = service.createCompany(messageId,
                    recommendationCreatedWithCompanyEvent.getCompanyName(),
                    recommendationCreatedWithCompanyEvent.getFirstCapital());
            CompanyResponseDto companyName = service.getCompanyNameById(company.getId());
            RecommendationWithCompanyResponseDto forCreateDto =
                    new RecommendationWithCompanyResponseDto(
                            companyName.getId(),
                            companyName.getMessageId(),
                            companyName.getCompany(),
                            companyName.getFirstCapital(),
                            recommendationCreatedWithCompanyEvent.getFullName(),
                            recommendationCreatedWithCompanyEvent.getPositionId(),
                            recommendationCreatedWithCompanyEvent.getRecommendation()
                    );

            SendResult<String, RecommendationWithCompanyResponseDto> result = null;
            try {
                result = kafkaTemplate2
                        .send("company-created-with-company-events-topic", company.getId().toString(), forCreateDto).get();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new NonRetryableException(e.getMessage());
            }

            LOGGER.info("Topic RecommendationCreatedWithCompanyEvent RecommendationWithCompanyResponseDto: {}", result.getRecordMetadata().topic());
            LOGGER.info("Partition RecommendationCreatedWithCompanyEvent RecommendationWithCompanyResponseDto: {}", result.getRecordMetadata().partition());
            LOGGER.info("Offset RecommendationCreatedWithCompanyEvent RecommendationWithCompanyResponseDto: {}", result.getRecordMetadata().offset());

            LOGGER.info("Return RecommendationCreatedWithCompanyEventCompanyResponseForCreateDto: {}", company);
        } catch (DataIntegrityViolationException e) {
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }

    private void createAnswerBack(RecommendationResponseDto recommendationDto) {
        Long companyId = recommendationDto.getCompanyId();
        CompanyResponseDto company = service.getCompanyNameById(companyId);

        SendResult<String, CompanyResponseDto> result = null;
        try {
            result = kafkaTemplate
                    .send(TOPIC, companyId.toString(), company).get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new NonRetryableException(e.getMessage());
        }

        LOGGER.info("Topic createAnswerBack: {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition createAnswerBack: {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset createAnswerBack: {}", result.getRecordMetadata().offset());

        LOGGER.info("Return createAnswerBack: {}", company);
    }

    @KafkaHandler
    public void handle(CompanyUpdatedEvent companyUpdatedEvent) {
        LOGGER.info("Received event CompanyUpdatedEvent: {}", companyUpdatedEvent.getCompany());

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response CompanyUpdatedEvent: {}", response.getBody());
            }
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage());
            throw new RetryableException(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }
}
