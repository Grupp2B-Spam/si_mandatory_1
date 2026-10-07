package graphQL.entities;

import jakarta.persistence.*;

@Entity
@Table(name ="tpublishingcompany")
public class PublishingCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nPublishingCompanyID")
    private Integer id;

    @Column(name = "cName", nullable = false, length = 40)
    private String name;

    public PublishingCompany() {

    }

    public PublishingCompany(String name) {
        this.name = name;
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
}
