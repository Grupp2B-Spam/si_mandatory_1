package graphQL.api;

import java.util.List;

import graphQL.dto.AuthorDto;
import graphQL.dto.PublishingCompanyDto;
import graphQL.input.PublisherInput;
import graphQL.service.PublishingCompanyService;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class PublisherController {

    PublishingCompanyService publisherService;

    public PublisherController(PublishingCompanyService publisher) {
        this.publisherService = publisher;
    }

    @Query("publishers")
    public @NonNull List<@NonNull PublishingCompanyDto> getPublishers(){
        return publisherService.listAll();
    }

    @Mutation("createPublisher")
    public PublishingCompanyDto createPublisher(@NonNull PublisherInput input) {
        return publisherService.create(input.name());
    }

    @Mutation("updatePublisher")
    public PublishingCompanyDto updatePublisher(@NonNull Integer id, @NonNull PublisherInput input) {
        return publisherService.update(id, input.name());
    }

    @Mutation("deletePublisher")
    public PublishingCompanyDto deletePublisher(@NonNull Integer id) {
        return publisherService.delete(id);
    }

    // Field resolvers:

}
