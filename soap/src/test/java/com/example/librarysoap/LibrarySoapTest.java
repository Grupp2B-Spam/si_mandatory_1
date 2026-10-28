package com.example.librarysoap;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import static org.assertj.core.api.Assertions.assertThat;

// These tests send real HTTP SOAP requests, just as Postman does.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LibrarySoapTest {
    private static final String NS = "http://example.com/library";
    @Autowired TestRestTemplate http;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void clearTestDatabase() {
        jdbc.update("DELETE FROM tbook");
        jdbc.update("DELETE FROM tauthor");
        jdbc.update("DELETE FROM tpublishingcompany");
    }

    @Test
    void allOperationsAndReferenceConflicts() throws Exception {
        int author = createAuthor();
        int company = createCompany();
        assertThat(value(ok("GetAuthorById", field("id", author)), "name")).isEqualTo("Ada");
        assertThat(value(ok("ListAuthors", ""), "id")).isEqualTo(String.valueOf(author));
        assertSuccess("UpdateAuthor", field("id", author) + field("name", "Grace") + field("surname", "Hopper"));
        assertThat(value(ok("GetAuthorById", field("id", author)), "name")).isEqualTo("Grace");

        assertThat(value(ok("GetPublishingCompanyById", field("id", company)), "name")).isEqualTo("Student Press");
        assertThat(value(ok("ListPublishingCompanies", ""), "id")).isEqualTo(String.valueOf(company));
        assertSuccess("UpdatePublishingCompany", field("id", company) + field("name", "Updated Press"));
        assertThat(value(ok("GetPublishingCompanyById", field("id", company)), "name")).isEqualTo("Updated Press");

        int book = Integer.parseInt(value(ok("CreateBook", bookFields(author, company, "1900")), "id"));
        Document fetched = ok("GetBookById", field("id", book));
        assertThat(value(fetched, "title")).isEqualTo("SOAP basics");
        assertThat(value(fetched, "authorId")).isEqualTo(String.valueOf(author));
        assertThat(value(fetched, "publishingCompanyId")).isEqualTo(String.valueOf(company));
        assertThat(value(fetched, "publishingYear")).isEqualTo("1900");
        assertSuccess("UpdateBook", field("id", book) + bookFields(author, company, "2025"));
        assertThat(value(ok("GetBookById", field("id", book)), "publishingYear")).isEqualTo("2025");

        fault("DeleteAuthor", field("id", author), "ConflictFault");
        fault("DeletePublishingCompany", field("id", company), "ConflictFault");
        assertSuccess("DeleteBook", field("id", book));
        assertSuccess("DeleteAuthor", field("id", author));
        assertSuccess("DeletePublishingCompany", field("id", company));
        fault("GetBookById", field("id", book), "NotFoundFault");
        assertThat(value(ok("ListAuthors", ""), "author")).isEmpty();
        assertThat(value(ok("ListPublishingCompanies", ""), "publishingCompany")).isEmpty();
    }

    @Test
    void rejectsInvalidFieldsAndReferencesWithoutWritingToDatabase() throws Exception {
        int author = createAuthor();
        int company = createCompany();
        fault("CreateBook", bookFields(author, company, "1899"), "ValidationFault");
        fault("CreateBook", bookFields(author, company, "abc"), "ValidationFault");
        fault("CreateBook", bookFields(author, company, "2147483648"), "ValidationFault");
        fault("CreateBook", bookFields(author, company, "2024").replace(field("title", "SOAP basics"), ""), "ValidationFault");
        fault("CreateBook", bookFields(author, company, "2024").replace(field("authorId", author), ""), "ValidationFault");
        fault("CreateBook", bookFields(author, company, "2024").replace(field("publishingCompanyId", company), ""), "ValidationFault");
        fault("CreateBook", bookFields(author, company, "2024").replace(field("publishingYear", "2024"), ""), "ValidationFault");
        fault("CreateBook", bookFields(2147483647, company, "2024"), "ValidationFault");
        fault("CreateBook", bookFields(author, 2147483647, "2024"), "ValidationFault");
        fault("CreateAuthor", field("name", "   ") + field("surname", "Example"), "ValidationFault");
        fault("CreateAuthor", field("name", "Ada"), "ValidationFault");
        fault("CreateAuthor", field("name", "x".repeat(41)) + field("surname", "Example"), "ValidationFault");
        fault("CreatePublishingCompany", field("name", "   "), "ValidationFault");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tbook", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tauthor", Integer.class)).isEqualTo(1);
    }

    @Test
    void rejectsInvalidUpdatesAndPreservesExistingData() throws Exception {
        int author = createAuthor();
        int company = createCompany();
        int book = Integer.parseInt(value(ok("CreateBook", bookFields(author, company, "2024")), "id"));
        fault("UpdateAuthor", field("id", author) + field("name", " ") + field("surname", "Example"), "ValidationFault");
        fault("UpdatePublishingCompany", field("id", company) + field("name", " "), "ValidationFault");
        fault("UpdateBook", field("id", book) + bookFields(author, company, "1899"), "ValidationFault");
        fault("UpdateBook", field("id", book) + bookFields(2147483647, company, "2025"), "ValidationFault");
        fault("UpdateBook", field("id", book) + bookFields(author, 2147483647, "2025"), "ValidationFault");
        assertThat(value(ok("GetAuthorById", field("id", author)), "name")).isEqualTo("Ada");
        assertThat(value(ok("GetPublishingCompanyById", field("id", company)), "name")).isEqualTo("Student Press");
        assertThat(value(ok("GetBookById", field("id", book)), "publishingYear")).isEqualTo("2024");
    }

    @Test
    void missingTargetsReturnNotFound() throws Exception {
        for (String entity : new String[]{"Book", "Author", "PublishingCompany"}) {
            fault("Get" + entity + "ById", field("id", 2147483647), "NotFoundFault");
            fault("Delete" + entity, field("id", 2147483647), "NotFoundFault");
        }
        fault("UpdateAuthor", field("id", 2147483647) + field("name", "Ada") + field("surname", "Lovelace"), "NotFoundFault");
        fault("UpdatePublishingCompany", field("id", 2147483647) + field("name", "Example"), "NotFoundFault");
        int author = createAuthor();
        int company = createCompany();
        fault("UpdateBook", field("id", 2147483647) + bookFields(author, company, "2024"), "NotFoundFault");
    }

    @Test
    void publishesWsdlAndItsImportedSchema() {
        ResponseEntity<String> wsdl = http.getForEntity("/soap/library.wsdl", String.class);
        assertThat(wsdl.getStatusCode().value()).isEqualTo(200);
        assertThat(wsdl.getBody()).contains("CreateBook", "ConflictFault", "librarySchema.xsd");
        ResponseEntity<String> schema = http.getForEntity("/soap/librarySchema.xsd", String.class);
        assertThat(schema.getStatusCode().value()).isEqualTo(200);
        assertThat(schema.getBody()).contains("minInclusive", "1900", "PublishingCompany");
    }

    private int createAuthor() throws Exception {
        return Integer.parseInt(value(ok("CreateAuthor", field("name", "Ada") + field("surname", "Lovelace")), "id"));
    }

    private int createCompany() throws Exception {
        return Integer.parseInt(value(ok("CreatePublishingCompany", field("name", "Student Press")), "id"));
    }

    private String bookFields(int author, int company, String year) {
        return field("title", "SOAP basics") + field("authorId", author)
                + field("publishingCompanyId", company) + field("publishingYear", year);
    }

    private void assertSuccess(String operation, String fields) throws Exception {
        assertThat(value(ok(operation, fields), "success")).isEqualTo("true");
    }

    private Document ok(String operation, String fields) throws Exception {
        ResponseEntity<String> response = send(operation, fields);
        assertThat(response.getStatusCode().value()).as(response.getBody()).isEqualTo(200);
        Document xml = parse(response.getBody());
        assertThat(xml.getElementsByTagNameNS(NS, operation + "Response").getLength()).isEqualTo(1);
        return xml;
    }

    private void fault(String operation, String fields, String faultName) throws Exception {
        ResponseEntity<String> response = send(operation, fields);
        assertThat(response.getStatusCode().value()).as(response.getBody()).isEqualTo(500);
        Document xml = parse(response.getBody());
        assertThat(xml.getElementsByTagNameNS(NS, faultName).getLength()).as(response.getBody()).isEqualTo(1);
        assertThat(value(xml, "message")).isNotBlank();
    }

    private ResponseEntity<String> send(String operation, String fields) {
        String body = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:lib=\"" + NS + "\">"
                + "<soap:Body><lib:" + operation + "Request>" + fields + "</lib:" + operation + "Request></soap:Body></soap:Envelope>";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/xml; charset=utf-8"));
        headers.set("SOAPAction", "\"" + NS + "/" + operation + "\"");
        return http.postForEntity("/soap/library", new HttpEntity<>(body, headers), String.class);
    }

    private static String field(String name, Object value) {
        return "<lib:" + name + ">" + value + "</lib:" + name + ">";
    }

    private static Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private static String value(Document xml, String element) throws Exception {
        return XPathFactory.newInstance().newXPath().evaluate("string(//*[local-name()='" + element + "'])", xml);
    }
}
