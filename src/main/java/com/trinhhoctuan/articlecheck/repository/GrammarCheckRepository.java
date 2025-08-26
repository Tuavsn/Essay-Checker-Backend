package com.trinhhoctuan.articlecheck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trinhhoctuan.articlecheck.model.GrammarCheck;

@Repository
public interface GrammarCheckRepository extends JpaRepository<GrammarCheck, Long> {
    List<GrammarCheck> findByEssayId(Long essayId);
    List<GrammarCheck> findByEssayIdAndIsFixed(Long essayId, Boolean isFixed);
}
