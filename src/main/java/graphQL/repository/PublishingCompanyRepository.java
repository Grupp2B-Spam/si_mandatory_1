package graphQL.repository;

import graphQL.entities.PublishingCompany;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PublishingCompanyRepository implements PanacheRepositoryBase<PublishingCompany, Integer> {
}
