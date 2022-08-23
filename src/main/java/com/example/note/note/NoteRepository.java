package com.example.note.note;

import com.example.note.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserOrderByIdDesc(User user);
    Note findByIdAndUser(Long id, User user);
}
