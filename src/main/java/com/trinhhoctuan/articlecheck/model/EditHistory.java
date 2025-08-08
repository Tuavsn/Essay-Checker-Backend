package com.trinhhoctuan.articlecheck.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "edit_histories")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EditHistory extends BaseModel {
    @Column(columnDefinition = "TEXT")
    private String previousContent;
    
    @Column(columnDefinition = "TEXT")
    private String newContent;
    
    @Column
    private String changeDescription;
    
    @Enumerated(EnumType.STRING)
    private ChangeType changeType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_id")
    private Essay essay;
    
    // Enum for change types
    public enum ChangeType {
        GRAMMAR_FIX,
        SPELLING_FIX,
        STYLE_IMPROVEMENT,
        PLAGIARISM_FIX,
        MANUAL_EDIT,
        AI_SUGGESTION
    }
}
