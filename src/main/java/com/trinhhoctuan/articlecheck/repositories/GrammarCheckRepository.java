package com.trinhhoctuan.articlecheck.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trinhhoctuan.articlecheck.models.GrammarCheck;

@Repository
public interface GrammarCheckRepository extends JpaRepository<GrammarCheck, Long> {
    List<GrammarCheck> findByEssayId(Long essayId);
    List<GrammarCheck> findByEssayIdAndIsFixed(Long essayId, Boolean isFixed);
}
