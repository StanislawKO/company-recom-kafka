package com.stas_kozh.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDto {
    private Long id;
    private String fullName;
    private Long companyId;
    private Long positionId;
    private String recommendation;

    public RecommendationResponseDto(String fullName, Long companyId, Long positionId, String recommendation) {
        this.fullName = fullName;
        this.companyId = companyId;
        this.positionId = positionId;
        this.recommendation = recommendation;
    }
}
