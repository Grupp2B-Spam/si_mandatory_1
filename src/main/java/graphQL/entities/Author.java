package graphQL.entities;

import jakarta.persistence.*;

@Entity
@Table(name ="tauthor")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nAuthorID")
    private Integer id;

    @Column(name = "cName", nullable = false, length = 40)
    private String name;

    @Column(name = "cSurname", length = 60)
    private String surname;

    public Author() {

    }

    public Author(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
