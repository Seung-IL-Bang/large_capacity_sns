package com.example.fastcampusmysql.domain.post.repository;

import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.util.PageHelper;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PostRepository {

    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    final static String TABLE = "Post";

    final static RowMapper<Post> rowMapper = (ResultSet resultSet, int rowNum) -> Post.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .contents(resultSet.getString("contents"))
            .createdDate(resultSet.getObject("createdDate", LocalDate.class))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .likeCount(resultSet.getLong("likeCount"))
            .version(resultSet.getLong("version"))
            .build();

    final static private RowMapper<DailyPostCount> DAILY_POST_COUNT_ROW_MAPPER
            = (ResultSet resultSet, int rowNum) -> new DailyPostCount(
            resultSet.getLong("memberId"),
            resultSet.getObject("createdDate", LocalDate.class),
            resultSet.getLong("count")
    );

    public void bulkInsert(List<Post> posts) {
        String sql = String.format("""
                INSERT INTO %s (memberId, contents, createdDate, createdAt)
                VALUES (:memberId, :contents, :createdDate, :createdAt)
                """, TABLE);

        SqlParameterSource[] params = posts.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }

    public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
        String sql = String.format("""
                SELECT memberId, createdDate, count(id) as count
                FROM %s
                WHERE memberId = :memberId and createdDate between :firstDate and :lastDate
                GROUP BY memberId, createdDate
                """, TABLE);

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(request);
        return namedParameterJdbcTemplate.query(sql, param, DAILY_POST_COUNT_ROW_MAPPER);
    }

    public Page<Post> findAllByMemberId(Long memberId, Pageable pageable) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());


        String sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY %s
                LIMIT :size
                OFFSET :offset
                """, TABLE, PageHelper.orderBy(pageable.getSort()));

        List<Post> posts = namedParameterJdbcTemplate.query(sql, params, rowMapper);
        return new PageImpl(posts, pageable, getCount(memberId));
    }

    public List<Post> findAllByMemberIdInAndOrderByIdDesc(Long memberId, int size) {

        String sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", size);
        return namedParameterJdbcTemplate.query(sql, param, rowMapper);
    }

    public List<Post> findAllByInMemberIdInAndOrderByIdDesc(List<Long> memberIds, int size) {

        if (memberIds.isEmpty()) {
            return List.of();
        }

        String sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId in (:memberIds)
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("memberIds", memberIds)
                .addValue("size", size);
        return namedParameterJdbcTemplate.query(sql, param, rowMapper);
    }
    public List<Post> findAllByLessThanIdAndMemberIdInAndOrderByIdDesc(Long id, Long memberId, int size) {
        String sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId and id < :id
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("id", id)
                .addValue("size", size);
        return namedParameterJdbcTemplate.query(sql, param, rowMapper);

    }
    public List<Post> findAllByLessThanIdAndInMemberIdInAndOrderByIdDesc(Long id, List<Long> memberIds, int size) {
        String sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId in (:memberIds) and id < :id
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("memberIds", memberIds)
                .addValue("id", id)
                .addValue("size", size);
        return namedParameterJdbcTemplate.query(sql, param, rowMapper);

    }

    public List<Post> findAllByInId(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        String sql = String.format("""
                SELECT *
                FROM %s
                WHERE id in (:ids)
                """, TABLE);
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("ids", ids);
        return namedParameterJdbcTemplate.query(sql, param, rowMapper);

    }

    public Optional<Post> findById(Long postId, Boolean requiredLock) {
        String sql = String.format("""
                SELECT * FROM %s WHERE id = :postId
                """, TABLE);
        if (requiredLock) { // Boolean을 파라미터로 받는 이유는 모든 조회 쿼리에 대해 락을 얻는 것은 성능 저하가 발생하기 때문이다.
            sql += " FOR UPDATE";
        }
        MapSqlParameterSource param = new MapSqlParameterSource().addValue("postId", postId);
        Post post = namedParameterJdbcTemplate.queryForObject(sql, param, rowMapper);
        return Optional.ofNullable(post);
    }

    private Long getCount(Long memberId) {
        String sql = String.format("""
                SELECT count(id)
                FROM %s
                WHERE memberId = :memberId
                """, TABLE);
        MapSqlParameterSource param = new MapSqlParameterSource().addValue("memberId", memberId);
        return namedParameterJdbcTemplate.queryForObject(sql, param, Long.class);
    }


    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        }
        return update(post);
    }

    private Post insert(Post post) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource param = new BeanPropertySqlParameterSource(post);

        long id = simpleJdbcInsert.executeAndReturnKey(param).longValue();


        return Post.builder()
                .id(id)
                .memberId(post.getMemberId())
                .contents(post.getContents())
                .createdDate(post.getCreatedDate())
                .createdAt(post.getCreatedAt())
                .build();

    }

    private Post update(Post post) {
        String sql = String.format("""
                UPDATE %s SET
                    memberId = :memberId,
                    contents = :contents,
                    likeCount = :likeCount,
                    createdDate = :createdDate,
                    createdAt = :createdAt,
                    version = :version + 1
                WHERE id = :id and version = :version
                """, TABLE);
        SqlParameterSource param = new BeanPropertySqlParameterSource(post);
        int updateRowCount = namedParameterJdbcTemplate.update(sql, param); // 반환값으로 업데이트된 레코드 수를 반환한다.
        if (updateRowCount == 0) {
            throw new RuntimeException("갱신 실패");
        }
        return post;
    }
}
