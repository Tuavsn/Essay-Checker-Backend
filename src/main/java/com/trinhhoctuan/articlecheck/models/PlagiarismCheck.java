package com.trinhhoctuan.articlecheck.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "plagiarism_checks")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlagiarismCheck extends BaseModel {
    @Column(nullable = false)
    private String matchedText;
    
    @Column
    private String sourceUrl;
    
    @Column
    private String sourceName;
    
    @Column(nullable = false)
    private Double similarityScore;
    
    @Column(nullable = false)
    private Integer startPosition;
    
    @Column(nullable = false)
    private Integer endPosition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_id")
    private Essay essay;
}
