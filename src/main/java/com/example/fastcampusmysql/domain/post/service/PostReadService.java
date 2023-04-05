package com.example.fastcampusmysql.domain.post.service;

import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostReadService  {

    final private PostRepository postRepository;

    public List<DailyPostCount> getDailyPostCounts(DailyPostCountRequest request) {
        /**
         * 반환값 -> 리스트 [작성일자, 작성회원, 작성 게시물 갯수]
         SELECT createdDate, memberId, count(id) AS count
         FROM %s
         WHERE memberId = :memberId AND createdDate BETWEEN :firstDate AND :lastDate
         GROUP BY memberId, createdDate
         */

        return postRepository.groupByCreatedDate(request);
    }
}
