package com.example.note.user;

public class AlreadyRegisteredUserException extends RuntimeException{
    // 이미 등록된 유저를 재등록하려고 할때 발생하는 Exception.
    public AlreadyRegisteredUserException(String message){
        super(message);
    }

    public AlreadyRegisteredUserException(){
        super("이미 등록된 사용자입니다.");
    }
}
