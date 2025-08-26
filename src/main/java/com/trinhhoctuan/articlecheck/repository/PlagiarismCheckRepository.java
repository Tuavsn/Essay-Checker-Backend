package com.trinhhoctuan.articlecheck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trinhhoctuan.articlecheck.model.PlagiarismCheck;

@Repository
public interface PlagiarismCheckRepository extends JpaRepository<PlagiarismCheck, Long> {
    List<PlagiarismCheck> findByEssayId(Long essayId);
    List<PlagiarismCheck> findByEssayIdAndSimilarityScoreGreaterThan(Long essayId, Double threshold);
}
