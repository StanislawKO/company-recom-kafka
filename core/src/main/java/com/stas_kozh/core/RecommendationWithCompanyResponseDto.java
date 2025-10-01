package com.stas_kozh.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationWithCompanyResponseDto {
    private Long id;
    private String messageId;
    private String company;
    private Long firstCapital;

    private String fullName;
    private Long positionId;
    private String recommendation;
}
