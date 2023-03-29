package com.example.fastcampusmysql.domain.dto;

import java.time.LocalDate;

public record MemberDto(
        Long id,
        String email,
        String nickname,
        LocalDate birthDay
) {
}
