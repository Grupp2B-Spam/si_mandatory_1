package graphQL.repository;

import graphQL.entities.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookRepository implements PanacheRepositoryBase<Book, Integer> {
    public boolean existsByAuthorId(Integer authorId) {
        if (authorId == null) {
            return false;
        }
        return count("author.id", authorId) > 0;
    }

    public boolean existsByPublisherId(Integer publisherId) {
        if (publisherId == null) {
            return false;
        }
        return count("publishingCompany.id", publisherId) > 0;
    }
}
