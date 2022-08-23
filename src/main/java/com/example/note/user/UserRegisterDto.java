package com.example.note.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserRegisterDto {
    // User 회원가입용 Dto.
    private String username;
    private String password;
}
