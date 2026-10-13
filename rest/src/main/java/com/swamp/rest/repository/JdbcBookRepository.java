package com.swamp.rest.repository;

import com.swamp.rest.model.records.Book;
import com.swamp.rest.onion_interfaces.BookRepository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {
    private final JdbcClient jdbcClient;

    public JdbcBookRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Book create(Book book) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("""
                insert into tbook (cTitle, nAuthorID, nPublishingYear, nPublishingCompanyID)
                values (:title, :authorId, :publishingYear, :publishingCompanyId)
                """)
                .param("title", book.title())
                .param("authorId", book.authorId())
                .param("publishingYear", book.publishingYear())
                .param("publishingCompanyId", book.publishingCompanyId())
                .update(keyHolder);

        return new Book(keyHolder.getKey().longValue(), book.title(), book.authorId(),
                book.publishingYear(), book.publishingCompanyId());
    }

    @Override
    public Optional<Book> findById(Long id) {
        return jdbcClient.sql("""
                select nBookID, cTitle, nAuthorID, nPublishingYear, nPublishingCompanyID
                from tbook
                where nBookID = :id
                """)
                .param("id", id)
                .query((rs, rowNum) -> new Book(
                        rs.getLong("nBookID"),
                        rs.getString("cTitle"),
                        rs.getLong("nAuthorID"),
                        rs.getObject("nPublishingYear", Integer.class),
                        rs.getLong("nPublishingCompanyID")))
                .optional();
    }

    @Override
    public List<Book> findAll(int limit, int offset) {
        return jdbcClient.sql("""
                select nBookID, cTitle, nAuthorID, nPublishingYear, nPublishingCompanyID
                from tbook
                order by nBookID
                limit :limit offset :offset
                """)
                .param("limit", limit)
                .param("offset", offset)
                .query((rs, rowNum) -> new Book(
                        rs.getLong("nBookID"),
                        rs.getString("cTitle"),
                        rs.getLong("nAuthorID"),
                        rs.getObject("nPublishingYear", Integer.class),
                        rs.getLong("nPublishingCompanyID")))
                .list();
    }

    @Override
    public long count() {
        return jdbcClient.sql("select count(*) from tbook")
                .query(Long.class)
                .single();
    }

    @Override
    public boolean update(Long id, Book book) {
        int updated = jdbcClient.sql("""
                update tbook
                set cTitle = :title,
                    nAuthorID = :authorId,
                    nPublishingYear = :publishingYear,
                    nPublishingCompanyID = :publishingCompanyId
                where nBookID = :id
                """)
                .param("id", id)
                .param("title", book.title())
                .param("authorId", book.authorId())
                .param("publishingYear", book.publishingYear())
                .param("publishingCompanyId", book.publishingCompanyId())
                .update();

        return updated > 0;
    }

    @Override
    public boolean delete(Long id) {
        int deleted = jdbcClient.sql("delete from tbook where nBookID = :id")
                .param("id", id)
                .update();

        return deleted > 0;
    }
}
