package com.practice.spring.entity.noteRevision;

import com.practice.spring.entity.note.Note;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "note_revision")
@Getter
@Setter
public class NoteRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    @Column(name = "old_title")
    private String oldTitle;

    @Column(name = "old_text")
    private String oldText;

    @CreationTimestamp
    @Column(name = "changed_at")
    private Instant changedAt;

    public NoteRevision(){}

    public NoteRevision(Note note) {
        this.note = note;
        this.oldTitle = note.getTitle();
        this.oldText = note.getText();
    }
}
