package com.trinhhoctuan.articlecheck.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trinhhoctuan.articlecheck.models.Essay;
import com.trinhhoctuan.articlecheck.models.Essay.EssayStatus;

@Repository
public interface EssayRepository extends JpaRepository<Essay, Long> {
    List<Essay> findByUserId(Long userId);
    List<Essay> findByStatus(EssayStatus status);
    List<Essay> findByUserIdOrderByCreatedAtDesc(Long userId);
}
