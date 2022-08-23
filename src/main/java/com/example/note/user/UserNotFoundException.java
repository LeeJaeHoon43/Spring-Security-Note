package com.example.note.user;

public class UserNotFoundException extends RuntimeException{
    // 사용자를 찾을 수 없을 때 발생하는 Exception.

    public UserNotFoundException(String message){
        super(message);
    }

    public UserNotFoundException(){
        super("사용자를 찾을 수 없습니다.");
    }
}
