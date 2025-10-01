package com.stas_kozh.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCreatedWithCompanyEvent {
    private Long id;
    private String fullName;
    private String companyName;
    private Long firstCapital;
    private Long positionId;
    private String recommendation;
}
