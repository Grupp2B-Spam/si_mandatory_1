package com.swamp.rest.repository;

import com.swamp.rest.model.records.Book;
import com.swamp.rest.onion_interfaces.BookRepository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {
    private final NamedParameterJdbcTemplate jdbcClient;

    public JdbcBookRepository(NamedParameterJdbcTemplate jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public long create(Book book) {
        final String sql = """
                insert into tbook (cTitle, nAuthorID, nPublishingYear, nPublishingCompanyID)
                values (:title, :authorId, :publishingYear, :publishingCompanyId)
                """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource param = new MapSqlParameterSource()
            .addValue("title", book.title())
                .addValue("authorId", book.authorId())
                .addValue("publishingYear", book.publishingYear())
                .addValue("publishingCompanyId", book.publishingCompanyId());

        
        jdbcClient.update(sql, param, keyHolder);
        
        return (int) keyHolder.getKeys().get("nbookid");
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = """
                select nBookID, cTitle, nAuthorID, nPublishingYear, nPublishingCompanyID
                from tbook
                where nBookID = :id
                """;
        try {
            Book shop = jdbcClient.queryForObject(
                sql,
                Collections.singletonMap("id", id),
                (rs, rowNum) -> new Book(
                        rs.getLong("nBookID"),
                        rs.getString("cTitle"),
                        rs.getLong("nAuthorID"),
                        rs.getObject("nPublishingYear", Integer.class),
                        rs.getLong("nPublishingCompanyID"))
            );
            return Optional.of(shop);
        } catch (EmptyResultDataAccessException e) {
            throw e;
        }
    }

    @Override
    public List<Book> findAll(int limit, int offset) {
        String sql = """
                select nBookID, cTitle, nAuthorID, nPublishingYear, nPublishingCompanyID
                from tbook
                order by nBookID
                limit :limit offset :offset
                """;

        SqlParameterSource param = new MapSqlParameterSource()
            .addValue("limit", limit)
                .addValue("offset", offset);
        try {
            return jdbcClient.query(
                sql,
                param,
                (rs, rowNum) -> new Book(
                        rs.getLong("nBookID"),
                        rs.getString("cTitle"),
                        rs.getLong("nAuthorID"),
                        rs.getObject("nPublishingYear", Integer.class),
                        rs.getLong("nPublishingCompanyID"))
            );
        } catch (EmptyResultDataAccessException e) {
            throw e;
        }
    }

    @Override
    public long count() {

        SqlParameterSource param = new MapSqlParameterSource();

        return jdbcClient.queryForObject("select count(*) from tbook", param, Long.class);
    }

    @Override
    public boolean update(Long id, Book book) {
        final String sql = """
                update tbook
                set cTitle = :title,
                    nAuthorID = :authorId,
                    nPublishingYear = :publishingYear,
                    nPublishingCompanyID = :publishingCompanyId
                where nBookID = :id
                """;

        SqlParameterSource param = new MapSqlParameterSource()
            .addValue("id", id)
            .addValue("title", book.title())
                .addValue("authorId", book.authorId())
                .addValue("publishingYear", book.publishingYear())
                .addValue("publishingCompanyId", book.publishingCompanyId());
        
        int updated = jdbcClient.update(sql, param);

        return updated > 0;
    }

    @Override
    public boolean delete(Long id) {
        final String sql = "delete from tbook where nBookID = :id";

        SqlParameterSource param = new MapSqlParameterSource()
            .addValue("id", id);

        int deleted = jdbcClient.update(sql, param);

        return deleted > 0;
    }
}
