package com.practice.spring.repository.noteEventLog;

import com.practice.spring.entity.noteEventLog.NoteEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteEventLogRepository extends JpaRepository<NoteEventLog, Long> {
    boolean existsByNoteId(Long noteId);
}
