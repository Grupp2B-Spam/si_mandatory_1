package com.swamp.rest.repository;

import com.swamp.rest.model.records.Author;
import com.swamp.rest.onion_interfaces.AuthorRepository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcAuthorRepository implements AuthorRepository {
    private final JdbcClient jdbcClient;

    public JdbcAuthorRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Author create(Author author) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("insert into tauthor (cName, cSurname) values (:name, :surname)")
                .param("name", author.name())
                .param("surname", author.surname())
                .update(keyHolder);

        long authorId = (int) keyHolder.getKeys().get("nauthorid");
        return new Author(authorId, author.name(), author.surname());
    }

    @Override
    public Optional<Author> findById(Long id) {
        return jdbcClient.sql("select nAuthorID, cName, cSurname from tauthor where nAuthorID = :id")
                .param("id", id)
                .query((rs, rowNum) -> new Author(
                        rs.getLong("nAuthorID"),
                        rs.getString("cName"),
                        rs.getString("cSurname")))
                .optional();
    }

    @Override
    public List<Author> findAll() {
        return jdbcClient.sql("select nAuthorID, cName, cSurname from tauthor order by nAuthorID")
                .query((rs, rowNum) -> new Author(
                        rs.getLong("nAuthorID"),
                        rs.getString("cName"),
                        rs.getString("cSurname")))
                .list();
    }

    @Override
    public boolean update(Long id, Author author) {
        int updated = jdbcClient.sql("update tauthor set cName = :name, cSurname = :surname where nAuthorID = :id")
                .param("id", id)
                .param("name", author.name())
                .param("surname", author.surname())
                .update();

        return updated > 0;
    }

    @Override
    public boolean delete(Long id) {
        int deleted = jdbcClient.sql("delete from tauthor where nAuthorID = :id")
                .param("id", id)
                .update();

        return deleted > 0;
    }
}
