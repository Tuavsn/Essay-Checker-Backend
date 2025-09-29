package com.trinhhoctuan.articlecheck.models;

import java.util.List;

import com.trinhhoctuan.articlecheck.enums.FileType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "essays")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Essay extends BaseModel {
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String originalContent;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String processedContent;

    @Column
    private String fileName;

    @Column
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Enumerated(EnumType.STRING)
    private EssayStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "essay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EditHistory> editHistories;

    @OneToMany(mappedBy = "essay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GrammarCheck> grammarChecks;

    @OneToMany(mappedBy = "essay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlagiarismCheck> plagiarismChecks;

    public enum EssayStatus {
        UPLOADED,
        PROCESSING,
        GRAMMAR_CHECKED,
        PLAGIARISM_CHECKED,
        COMPLETED,
        ERROR
    }
}
