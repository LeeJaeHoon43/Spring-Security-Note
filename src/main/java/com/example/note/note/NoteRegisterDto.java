package com.example.note.note;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRegisterDto {
    // 노트 등록 Dto.
    private String title;
    private String content;
}
