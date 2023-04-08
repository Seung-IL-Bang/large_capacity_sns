package com.example.fastcampusmysql.domain.post.repository;

import com.example.fastcampusmysql.domain.post.entity.PostLike;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Repository
public class PostLikeRepository {

    final static String TABLE = "PostLike";

    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    final static private RowMapper<PostLike> rowMapper = (ResultSet resultset, int rowNum) -> PostLike.builder()
            .id(resultset.getLong("id"))
            .postId(resultset.getLong("postId"))
            .memberId(resultset.getLong("memberId"))
            .createdAt(resultset.getObject("createdAt", LocalDateTime.class))
            .build();

    public PostLike save(PostLike postLike) {
        if (postLike.getId() == null) {
            return insert(postLike);
        }
        throw new UnsupportedOperationException("갱신을 지원하지 않습니다.");
    }

    private PostLike insert(PostLike postLike) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(postLike);

        long id = simpleJdbcInsert.executeAndReturnKey(param).longValue();

        return PostLike.builder()
                .id(id)
                .postId(postLike.getPostId())
                .memberId(postLike.getMemberId())
                .createdAt(postLike.getCreatedAt())
                .build();
    }

    public Long count(Long postId) {
        String sql = String.format("""
                SELECT count(id)
                FROM %s
                WHERE postId = :postId
                """, TABLE);
        MapSqlParameterSource param = new MapSqlParameterSource().addValue("postId", postId);
        return namedParameterJdbcTemplate.queryForObject(sql, param, Long.class);
    }

}
