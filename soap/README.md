# Library SOAP API — Spring Boot

This is a small contract-first SOAP 1.1 API for the group's existing library database.
It implements all 14 operations from the assignment. It uses Spring Boot, Spring Web
Services, JDBC and PostgreSQL. The other APIs can keep using their own frameworks.

## Run it

You need JDK 21, Docker and an internet connection for the first Maven build.
You do not need a separate Maven installation: this folder contains a Maven wrapper.

From the repository root:

```powershell
docker compose up -d
cd soap
.\mvnw.cmd spring-boot:run
```

Wait for the Spring Boot log to say that the application has started.
Maven downloads the dependencies on the first run into your own `.m2` cache.
Your group mates run the same commands; each machine downloads its own copy.
Commit `pom.xml` and the wrapper, rather than the downloaded libraries or `target/`.

If Maven picks the wrong Java version, point `JAVA_HOME` at your installed **JDK 21**
directory in that terminal, then run the command again. Do not include `/bin` in
`JAVA_HOME`. You can check what Maven uses with `.\mvnw.cmd -version`.

| Purpose | URL |
| --- | --- |
| POST SOAP requests | `http://localhost:8082/soap/library` |
| GET WSDL | `http://localhost:8082/soap/library.wsdl` |
| GET imported XSD | `http://localhost:8082/soap/librarySchema.xsd` |

The app uses the existing `library` database on port 5432, with `library` as its
username and password. Optional environment variables `DB_URL`, `DB_USERNAME` and
`DB_PASSWORD` override these settings. It does not create or alter the shared tables.

## Test with Postman

1. Import `../postman/library-soap.postman_collection.json`.
2. Start PostgreSQL and the SOAP application as above.
3. Open **Library SOAP - Student Walkthrough**.
4. Run the whole collection in order using Collection Runner, or send each numbered
   request in order yourself.

The collection's `baseUrl` variable defaults to `http://localhost:8082`. The create
requests save their generated IDs in collection variables, so you do not need to
copy IDs manually. The requests exercise every operation, check all three fault
types, and finally delete their example book, author and publishing company.

If you stop the collection midway, its records remain in the shared database.
Requests 24–26 clean up the IDs from that run; delete the book first.

All operations use **POST to the same URL**. The XML inside the SOAP body identifies
the operation. Each request contains:

```text
Content-Type: text/xml; charset=utf-8
SOAPAction: "http://example.com/library/CreateAuthor"
```

Change `CreateAuthor` in the action to match the operation. In **Body → raw → XML**,
the first request looks like this:

```xml
<soap:Envelope
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:lib="http://example.com/library">
    <soap:Body>
        <lib:CreateAuthorRequest>
            <lib:name>Ada</lib:name>
            <lib:surname>Lovelace</lib:surname>
        </lib:CreateAuthorRequest>
    </soap:Body>
</soap:Envelope>
```

The response body contains a generated ID, for example:

```xml
<lib:CreateAuthorResponse xmlns:lib="http://example.com/library">
    <lib:id>600</lib:id>
</lib:CreateAuthorResponse>
```

The actual ID depends on the database, and Spring wraps this payload in a SOAP
envelope. XML prefixes such as `lib` and `ns2` may differ; the namespace URI is what
matters. `http://example.com/library` identifies our XML vocabulary; it is not a
website that the application calls.

Updates replace all editable fields, rather than performing a partial update.
All update/delete responses contain `<success>true</success>` in our namespace.

## Follow one request through the code

Start with `CreateAuthor`; it has no foreign keys to complicate the example.

1. **`src/main/resources/wsdl/librarySchema.xsd`** defines `Author`,
   `CreateAuthorRequest` and `CreateAuthorResponse`. The request contains name and
   surname; the response contains an integer ID.
2. **`pom.xml`** runs the JAXB plugin when Maven builds the application. It generates
   matching Java classes under `target/generated-sources/jaxb/`. These classes are
   simple XML data containers. Do not edit them; change the XSD and rebuild instead.
3. **`config/WebServiceConfig.java`** registers Spring's SOAP servlet under `/soap/*`,
   publishes the WSDL and XSD, and installs request validation against the XSD.
4. **`endpoint/LibraryEndpoint.java`** uses `@PayloadRoot` to match the request's
   namespace and element name. Spring converts the XML into a generated
   `CreateAuthorRequest`. The endpoint calls `authors.create(...)`, puts the
   returned ID into `CreateAuthorResponse`, and returns it.
5. **`service/AuthorService.java`** checks that name and surname are not blank and
   fit the database column lengths. It then calls the repository. `@Transactional`
   makes a write operation succeed or roll back as a unit.
6. **`repository/AuthorRepository.java`** performs the parameterized SQL INSERT with
   `JdbcTemplate`. The database generates the ID, and `GeneratedKeyHolder` retrieves
   it. Parameter placeholders (`?`) keep input separate from SQL code.
7. Spring serializes the response object into XML and sends the SOAP response back
   to Postman.

The publishing company and book code follow the same endpoint → service → repository
pattern. Constructor injection gives each class the dependencies it needs, just as
in other Spring Boot applications.

## What each file is responsible for

Paths below are relative to `src/main/java/com/example/librarysoap/`, unless shown
otherwise.

| File | Responsibility |
| --- | --- |
| `SoapApplication.java` | Starts the Spring Boot application |
| `config/WebServiceConfig.java` | Registers the SOAP servlet, contract and validator |
| `endpoint/LibraryEndpoint.java` | Receives the 14 XML requests and builds responses |
| `service/BookService.java` | Book validation, reference checks and CRUD behavior |
| `service/AuthorService.java` | Author CRUD and deletion conflict checks |
| `service/PublishingCompanyService.java` | Publishing company CRUD and deletion conflict checks |
| `service/Validation.java` | Small shared check for required text and maximum lengths |
| `repository/BookRepository.java` | SQL for `tbook` |
| `repository/AuthorRepository.java` | SQL for `tauthor` |
| `repository/PublishingCompanyRepository.java` | SQL for `tpublishingcompany` |
| `fault/NotFoundException.java` | Missing requested entity |
| `fault/ValidationException.java` | Invalid field values or references |
| `fault/ConflictException.java` | Attempt to delete an entity referenced by a book |
| `fault/SoapFaultResolver.java` | Converts those exceptions into SOAP faults |
| `fault/RequestValidationInterceptor.java` | Converts XSD validation failures into `ValidationFault` |
| `src/main/resources/application.properties` | HTTP port and database connection |
| `src/main/resources/wsdl/librarySchema.xsd` | Types, messages and field restrictions |
| `src/main/resources/wsdl/library.wsdl` | Operations, declared faults and SOAP binding |

`@Endpoint` is Spring Web Services' equivalent of an entry point for SOAP messages.
It is different from `@RestController`: it receives XML payloads selected by their
qualified names, rather than mapping each operation to an HTTP URL and verb.

## Validation and faults

The XSD validates required elements, their order, integer formats, text lengths and
the minimum publishing year of 1900. Input text uses `xs:token`, which collapses
whitespace, with a minimum length of one. Keep elements in the order shown in the
collection because the XSD uses `xs:sequence`.

The service layer also checks values. In particular, an XSD cannot tell whether an
author or publishing company is present in our database, so `BookService` looks
them up before creating or updating a book. IDs are generated by the database.

For author/company deletion, the service checks for referencing books. The existing
database foreign keys also enforce this if another request changes data between
the check and deletion. A failed write throws an exception and rolls back.

| Situation | SOAP detail element |
| --- | --- |
| Get/update/delete targets an ID that does not exist | `NotFoundFault` |
| Required input missing, invalid integer, blank text, year below 1900 | `ValidationFault` |
| Book references an author/company that does not exist | `ValidationFault` |
| Delete an author/company referenced by a book | `ConflictFault` |

For example, a fault includes this inside the SOAP fault's `<detail>` element:

```xml
<lib:NotFoundFault xmlns:lib="http://example.com/library">
    <lib:message>Book 2147483647 does not exist</lib:message>
</lib:NotFoundFault>
```

SOAP 1.1 faults use **HTTP 500** here. Check the named detail element to distinguish
the three faults. An expected `NotFoundFault` with HTTP 500 is a passing fault test.
Malformed field values are rejected by the schema before the service is called;
therefore schema validation takes precedence over a missing target ID when both
are wrong. Unknown operations and unreadable SOAP envelopes are protocol errors,
rather than one of the assignment's business faults.

The existing shared database includes authors with empty surnames. Those existing
values can still be read; create/update requests require a nonblank surname.

## Automated tests

```powershell
cd soap  # if you are currently at the repository root
.\mvnw.cmd test
```

`LibrarySoapTest` starts the HTTP server on a random port and sends actual SOAP
requests. It checks the full CRUD flow, list operations, all fault categories,
invalid updates, required fields, bad integers, the 1900 boundary and WSDL/XSD URLs.
Tests use their own H2 database in PostgreSQL compatibility mode, so they do not
modify the group's PostgreSQL records. Running the Postman collection against the
normal application is the additional check against the actual PostgreSQL database.

To package the server instead of running in Maven:

```powershell
.\mvnw.cmd package
java -jar target/library-soap-1.0-SNAPSHOT.jar
```

Reference: Spring's SOAP guide explains this contract-first structure:
https://spring.io/guides/gs/producing-web-service
