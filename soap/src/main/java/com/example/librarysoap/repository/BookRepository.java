package com.example.librarysoap.repository;

import com.example.librarysoap.generated.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbc;

    public BookRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Book> rowMapper = (row, rowNumber) -> {
        Book entity = new Book();
        entity.setId(row.getInt("nbookid"));
        entity.setTitle(row.getString("ctitle"));
        entity.setAuthorId(row.getInt("nauthorid"));
        entity.setPublishingCompanyId(row.getInt("npublishingcompanyid"));
        entity.setPublishingYear(row.getInt("npublishingyear"));
        return entity;
    };

    public Optional<Book> findById(int id) {
        return jdbc.query("SELECT * FROM tbook WHERE nbookid = ?", rowMapper, id)
                .stream().findFirst();
    }

    public int create(String title, int authorId, int publishingCompanyId, int publishingYear) {
        // The database generates the ID. KeyHolder reads it after the INSERT.
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(
                    "INSERT INTO tbook (ctitle, nauthorid, npublishingcompanyid, npublishingyear) VALUES (?, ?, ?, ?)",
                    new String[]{"nbookid"});
            statement.setString(1, title);
            statement.setInt(2, authorId);
            statement.setInt(3, publishingCompanyId);
            statement.setInt(4, publishingYear);
            return statement;
        }, key);
        return key.getKey().intValue();
    }

    public int update(int id, String title, int authorId, int publishingCompanyId, int publishingYear) {
        return jdbc.update(
                "UPDATE tbook SET ctitle = ?, nauthorid = ?, npublishingcompanyid = ?, npublishingyear = ? WHERE nbookid = ?",
                title, authorId, publishingCompanyId, publishingYear, id);
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM tbook WHERE nbookid = ?", id);
    }

}
