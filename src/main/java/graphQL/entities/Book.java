package graphQL.entities;

import jakarta.persistence.*;

@Entity
@Table(name ="tbook")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nBookID")
    private Integer id;

    @Column(name = "cTitle", nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nAuthorID")
    private Author author;

    @Column(name = "nPublishingYear")
    private Integer publishingYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nPublishingcompanyID")
    private PublishingCompany publishingCompany;

    public Book() {

    }

    public Book(String title, Integer publishingYear, Author author, PublishingCompany publishingCompany) {
        this.title = title;
        this.publishingYear = publishingYear;
        this.author = author;
        this.publishingCompany = publishingCompany;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Integer getPublishingYear() {
        return publishingYear;
    }

    public void setPublishingYear(Integer publishingYear) {
        this.publishingYear = publishingYear;
    }

    public PublishingCompany getPublishingCompany() {
        return publishingCompany;
    }

    public void setPublishingCompany(PublishingCompany publishingCompany) {
        this.publishingCompany = publishingCompany;
    }
}
