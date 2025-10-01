package com.stas_kozh.recommendationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecommendationDto {
    private Long id;
    private String fullName;
    private Long companyId;
    private Long positionId;
    private String recommendation;
}
