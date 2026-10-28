package com.example.librarysoap.repository;

import com.example.librarysoap.generated.Author;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepository {
    private final JdbcTemplate jdbc;

    public AuthorRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Author> rowMapper = (row, rowNumber) -> {
        Author entity = new Author();
        entity.setId(row.getInt("nauthorid"));
        entity.setName(row.getString("cname"));
        entity.setSurname(row.getString("csurname"));
        return entity;
    };

    public Optional<Author> findById(int id) {
        return jdbc.query("SELECT * FROM tauthor WHERE nauthorid = ?", rowMapper, id)
                .stream().findFirst();
    }

    public List<Author> findAll() {
        return jdbc.query("SELECT * FROM tauthor ORDER BY nauthorid", rowMapper);
    }

    public int create(String name, String surname) {
        // The database generates the ID. KeyHolder reads it after the INSERT.
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(
                    "INSERT INTO tauthor (cname, csurname) VALUES (?, ?)",
                    new String[]{"nauthorid"});
            statement.setString(1, name);
            statement.setString(2, surname);
            return statement;
        }, key);
        return key.getKey().intValue();
    }

    public int update(int id, String name, String surname) {
        return jdbc.update(
                "UPDATE tauthor SET cname = ?, csurname = ? WHERE nauthorid = ?",
                name, surname, id);
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM tauthor WHERE nauthorid = ?", id);
    }

    public boolean isReferenced(int id) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM tbook WHERE nauthorid = ?)", Boolean.class, id));
    }

}
