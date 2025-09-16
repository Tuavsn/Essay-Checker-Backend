package com.trinhhoctuan.articlecheck.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trinhhoctuan.articlecheck.models.EditHistory;

@Repository
public interface EditHistoryRepository extends JpaRepository<EditHistory, Long> {
    List<EditHistory> findByEssayIdOrderByCreatedAtDesc(Long essayId);
}
