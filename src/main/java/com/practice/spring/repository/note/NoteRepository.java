package com.practice.spring.repository.note;

import com.practice.spring.dto.note.AuthorNoteSummary;
import com.practice.spring.entity.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("""
             select new com.practice.spring.dto.note.AuthorNoteSummary(count(n),max(n.createdAt))
             from Note n where n.author=:author""")
    Optional<AuthorNoteSummary> getAuthorSummary(@Param("author") String author);
}
