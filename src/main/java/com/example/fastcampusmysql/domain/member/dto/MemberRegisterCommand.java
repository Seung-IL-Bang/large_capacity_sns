package com.example.fastcampusmysql.domain.member.dto;

import java.time.LocalDate;

public record MemberRegisterCommand(String email,
                                    String nickname,
                                    LocalDate birthDay) {

}

/**
 * record: Java SDK 16 이상부터 공식 기능으로 제공됨
 * record 로 선언하면 getter 와 setter 가 기본으로 제공되고, property 접근 형식으로 사용할 수 있다.
 * 즉, .getEmail() 이 아니라 .email() 처럼 접근이 가능해진다.
 * 단순히 데이터만을 담는 용도이므로 dto 로 주로 사용한다.
 */