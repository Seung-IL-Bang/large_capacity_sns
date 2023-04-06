package com.example.fastcampusmysql.util;

public record  CursorRequest(Long key, int size) {
    public static final Long NONE_KEY = -1L; // 더 이상 데이터가 없을 때

    public Boolean hasKey() {
        return key != null;
    }

    public CursorRequest next(Long key) {
        return new CursorRequest(key, size);
    }
}
