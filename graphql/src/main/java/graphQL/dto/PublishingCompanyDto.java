package graphQL.dto;

import graphQL.entities.PublishingCompany;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;

@Name("Publisher")
public record PublishingCompanyDto(@NonNull Integer id, @NonNull String name) {

    public static PublishingCompanyDto from(PublishingCompany publishingCompany) {
        return new PublishingCompanyDto(publishingCompany.getId(), publishingCompany.getName());
    }
}
