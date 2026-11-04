package soapapi.api;

import soapapi.generated.*;
import soapapi.service.PublishingCompanyService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PublishingCompanyEndpoint {
    private static final String NAMESPACE = "http://example.com/library";
    private final PublishingCompanyService companies;

    public PublishingCompanyEndpoint(PublishingCompanyService companies) {
        this.companies = companies;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CreatePublishingCompanyRequest")
    @ResponsePayload
    public CreatePublishingCompanyResponse createPublishingCompany(@RequestPayload CreatePublishingCompanyRequest request) {
        CreatePublishingCompanyResponse response = new CreatePublishingCompanyResponse();
        response.setId(companies.create(request.getName()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetPublishingCompanyByIdRequest")
    @ResponsePayload
    public GetPublishingCompanyByIdResponse getPublishingCompanyById(@RequestPayload GetPublishingCompanyByIdRequest request) {
        GetPublishingCompanyByIdResponse response = new GetPublishingCompanyByIdResponse();
        response.setPublishingCompany(companies.getById(request.getId()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ListPublishingCompaniesRequest")
    @ResponsePayload
    public ListPublishingCompaniesResponse listPublishingCompanies(@RequestPayload ListPublishingCompaniesRequest request) {
        ListPublishingCompaniesResponse response = new ListPublishingCompaniesResponse();
        PublishingCompanyList list = new PublishingCompanyList();
        list.getPublishingCompany().addAll(companies.list());
        response.setPublishingCompanies(list);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "UpdatePublishingCompanyRequest")
    @ResponsePayload
    public UpdatePublishingCompanyResponse updatePublishingCompany(@RequestPayload UpdatePublishingCompanyRequest request) {
        UpdatePublishingCompanyResponse response = new UpdatePublishingCompanyResponse();
        companies.update(request.getId(), request.getName());
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "DeletePublishingCompanyRequest")
    @ResponsePayload
    public DeletePublishingCompanyResponse deletePublishingCompany(@RequestPayload DeletePublishingCompanyRequest request) {
        DeletePublishingCompanyResponse response = new DeletePublishingCompanyResponse();
        companies.delete(request.getId());
        response.setSuccess(true);
        return response;
    }
}
