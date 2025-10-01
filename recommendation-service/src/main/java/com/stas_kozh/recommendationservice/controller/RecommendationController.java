package com.stas_kozh.recommendationservice.controller;

import com.stas_kozh.core.CompanyResponseDto;
import com.stas_kozh.core.RecommendationResponseDto;
import com.stas_kozh.recommendationservice.dto.PositionResponseDto;
import com.stas_kozh.recommendationservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService service;
    private final Environment environment;

    @Value("${config.name}")
    public String name = "World";

    @GetMapping("/home")
    public String greetingMessage() {
        return service.greeting();
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<RecommendationResponseDto> getRecommendationById(
            @PathVariable Long recommendationId
    ) {
        RecommendationResponseDto recommendation = service
                .getRecommendationById(recommendationId);
        return ResponseEntity.ok(recommendation);
    }

    @GetMapping("/status/check")
    public String status() {
        System.out.println("NAME: " + name);
        return "Recommendation service is running on port: "
                + environment.getProperty("local.server.port")
                + " Name: " + name;
    }
}
