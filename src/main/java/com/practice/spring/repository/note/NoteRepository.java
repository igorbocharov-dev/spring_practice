package com.practice.spring.repository.note;

import com.practice.spring.entity.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    long countByAuthor(String author);

    @Query("select max(n.createdAt) from Note n where n.author=:author")
    Instant findLastCreatedAtByAuthor(@Param("author") String author);

    boolean existsByAuthor(String author);
}
