package com.example.fastcampusmysql.domain.post.repository;

import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostRepository {

    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    final static String TABLE = "Post";

    final static private RowMapper<DailyPostCount> DAILY_POST_COUNT_ROW_MAPPER
            = (ResultSet resultSet, int rowNum) -> new DailyPostCount(
            resultSet.getLong("memberId"),
            resultSet.getObject("createdDate", LocalDate.class),
            resultSet.getLong("count")
    );


    public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
        String sql = String.format("""
                SELECT createdDate, memberId, count(id) as count
                FROM %s
                WHERE memberId = :memberId and createdDate between :firstDate and :lastDate
                GROUP BY memberId, createdDate
                """, TABLE);

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(request);
        return namedParameterJdbcTemplate.query(sql, param, DAILY_POST_COUNT_ROW_MAPPER);
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        }
        throw new UnsupportedOperationException("Post는 갱신을 지원하지 않습니다.");
    }

    private Post insert(Post post) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName("Post")
                .usingGeneratedKeyColumns("id");

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(post);

        long id = simpleJdbcInsert.executeAndReturnKey(param).longValue();

        return Post.builder().
                id(id)
                .memberId(post.getMemberId())
                .contents(post.getContents())
                .createDate(post.getCreatedDate())
                .createdAt(post.getCreatedAt())
                .build();

    }
}
