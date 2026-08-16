package com.practice.spring.repository.note;

import com.practice.spring.entity.note.NoteRevision;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRevisionRepository extends JpaRepository<NoteRevision, Long> {

    Slice<NoteRevision> findAllBy(Pageable pageable);
}
