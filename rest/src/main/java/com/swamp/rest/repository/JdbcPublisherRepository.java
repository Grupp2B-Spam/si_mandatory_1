package com.swamp.rest.repository;

import com.swamp.rest.model.records.Publisher;
import com.swamp.rest.onion_interfaces.PublisherRepository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPublisherRepository implements PublisherRepository {
    private final JdbcClient jdbcClient;

    public JdbcPublisherRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Publisher create(Publisher publisher) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("insert into tpublishingcompany (cName) values (:name)")
                .param("name", publisher.name())
                .update(keyHolder);

        return new Publisher(keyHolder.getKey().longValue(), publisher.name());
    }

    @Override
    public Optional<Publisher> findById(Long id) {
        return jdbcClient.sql("""
                select nPublishingCompanyID, cName
                from tpublishingcompany
                where nPublishingCompanyID = :id
                """)
                .param("id", id)
                .query((rs, rowNum) -> new Publisher(
                        rs.getLong("nPublishingCompanyID"),
                        rs.getString("cName")))
                .optional();
    }

    @Override
    public List<Publisher> findAll() {
        return jdbcClient.sql("select nPublishingCompanyID, cName from tpublishingcompany order by nPublishingCompanyID")
                .query((rs, rowNum) -> new Publisher(
                        rs.getLong("nPublishingCompanyID"),
                        rs.getString("cName")))
                .list();
    }

    @Override
    public boolean update(Long id, Publisher publisher) {
        int updated = jdbcClient.sql("update tpublishingcompany set cName = :name where nPublishingCompanyID = :id")
                .param("id", id)
                .param("name", publisher.name())
                .update();

        return updated > 0;
    }

    @Override
    public boolean delete(Long id) {
        int deleted = jdbcClient.sql("delete from tpublishingcompany where nPublishingCompanyID = :id")
                .param("id", id)
                .update();

        return deleted > 0;
    }
}
