package com.stas_kozh.recommendationservice.handler;

import com.stas_kozh.core.CompanyResponseDto;
import com.stas_kozh.core.RecommendationResponseDto;
import com.stas_kozh.core.RecommendationWithCompanyResponseDto;
import com.stas_kozh.recommendationservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {
        "company-created-events-topic",
        "company-created-with-company-events-topic"
})
@RequiredArgsConstructor
public class CompanyCreateEventHandler {

    private final RecommendationService recommendationService;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @KafkaHandler
    public void handle(CompanyResponseDto companyResponseDto) {
        LOGGER.info("Received event CompanyResponseDto: {}", companyResponseDto.getCompany());
    }

    @KafkaHandler
    public void handle(RecommendationResponseDto recommendationDto) {
        LOGGER.info("Received event RecommendationResponseDto: {}", recommendationDto.getRecommendation());
    }

    @KafkaHandler
    public void handle(RecommendationWithCompanyResponseDto recommendationWithCompany) {
        recommendationService.createRecommendation(new RecommendationResponseDto(
                null,
                recommendationWithCompany.getFullName(),
                recommendationWithCompany.getId(),
                recommendationWithCompany.getPositionId(),
                recommendationWithCompany.getRecommendation()));
        LOGGER.info("Received event recommendationWithCompanyResponseDto: {}", recommendationWithCompany.getCompany());
    }
}
