package soapapi.repository;

import soapapi.generated.PublishingCompany;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PublishingCompanyRepository {
    private final JdbcTemplate jdbc;

    public PublishingCompanyRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<PublishingCompany> rowMapper = (row, rowNumber) -> {
        PublishingCompany entity = new PublishingCompany();
        entity.setId(row.getInt("npublishingcompanyid"));
        entity.setName(row.getString("cname"));
        return entity;
    };

    public Optional<PublishingCompany> findById(int id) {
        return jdbc.query("SELECT * FROM tpublishingcompany WHERE npublishingcompanyid = ?", rowMapper, id)
                .stream().findFirst();
    }

    public List<PublishingCompany> findAll() {
        return jdbc.query("SELECT * FROM tpublishingcompany ORDER BY npublishingcompanyid", rowMapper);
    }

    public int create(String name) {
        // The database generates the ID. KeyHolder reads it after the INSERT.
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(
                    "INSERT INTO tpublishingcompany (cname) VALUES (?)",
                    new String[]{"npublishingcompanyid"});
            statement.setString(1, name);
            return statement;
        }, key);
        return key.getKey().intValue();
    }

    public int update(int id, String name) {
        return jdbc.update(
                "UPDATE tpublishingcompany SET cname = ? WHERE npublishingcompanyid = ?",
                name, id);
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM tpublishingcompany WHERE npublishingcompanyid = ?", id);
    }

    public boolean isReferenced(int id) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM tbook WHERE npublishingcompanyid = ?)", Boolean.class, id));
    }

}
