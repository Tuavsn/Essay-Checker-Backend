package com.trinhhoctuan.articlecheck.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trinhhoctuan.articlecheck.models.IgnoreWords;

@Repository
public interface IgnoreWordsRepository extends JpaRepository<IgnoreWords, Long> {
  Optional<IgnoreWords> findByIdAndUserId(Long id, Long userId);
  List<IgnoreWords> findByUserIdOrderByCreatedAtDesc(Long userId);
  List<IgnoreWords> findByUserIdAndIsPublicOrderByCreatedAtDesc(Long userId, boolean isPublic);
}
