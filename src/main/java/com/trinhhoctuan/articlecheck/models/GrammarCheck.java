package com.trinhhoctuan.articlecheck.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "grammar_checks")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GrammarCheck extends BaseModel {
    @Column(nullable = false)
    private Integer startPosition;
    
    @Column(nullable = false)
    private Integer endPosition;
    
    @Column(nullable = false)
    private String errorText;
    
    @Column(nullable = false)
    private String ruleId;
    
    @Column(nullable = false)
    private String message;
    
    @Column
    private String suggestedReplacement;
    
    @Enumerated(EnumType.STRING)
    private ErrorSeverity severity;
    
    @Column
    @Builder.Default
    private Boolean isFixed = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_id")
    private Essay essay;

    // Enum for error severity levels
    public enum ErrorSeverity {
        LOW,
        MEDIUM,
        HIGH
    }
}
