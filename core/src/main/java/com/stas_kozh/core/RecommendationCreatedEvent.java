package com.stas_kozh.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCreatedEvent {
    private Long id;
    private String fullName;
    private Long companyId;
    private Long positionId;
    private String recommendation;
}
