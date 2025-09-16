package com.trinhhoctuan.articlecheck.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trinhhoctuan.articlecheck.models.PlagiarismCheck;

@Repository
public interface PlagiarismCheckRepository extends JpaRepository<PlagiarismCheck, Long> {
    List<PlagiarismCheck> findByEssayId(Long essayId);
    List<PlagiarismCheck> findByEssayIdAndSimilarityScoreGreaterThan(Long essayId, Double threshold);
}
