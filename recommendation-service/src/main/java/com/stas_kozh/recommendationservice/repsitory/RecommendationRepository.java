package com.stas_kozh.recommendationservice.repsitory;

import com.stas_kozh.recommendationservice.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
}
