# SOAP API

Spring Boot + Spring Web Services, using the shared PostgreSQL database.
Requires Java 21.

## Run

Start Docker Desktop, then from the repository root:

```powershell
docker compose up -d
cd soap
.\mvnw.cmd spring-boot:run
```

Maven downloads dependencies the first time. Each group member runs the same
commands on their machine; no shared Maven cache is needed.

- SOAP endpoint: `POST http://localhost:8082/soap/library`
- WSDL: `http://localhost:8082/soap/library.wsdl`
- XSD: `http://localhost:8082/soap/librarySchema.xsd`

Database settings are in `src/main/resources/application.properties`.
The app uses the existing tables and does not modify the database schema.

## Structure

Java code is under `src/main/java/soapapi/`:

- `SoapApiApplication`: starts Spring Boot.
- `api/`: one endpoint for books, authors and publishing companies.
- `service/`: CRUD behavior, database reference checks and deletion conflicts.
- `repository/`: SQL queries using Spring's `JdbcTemplate`.
- `config/`: SOAP servlet, WSDL/XSD publishing and request validation.
- `fault/`: the three exceptions and their SOAP fault responses.

The request flow is: **endpoint → service → repository → database**.

`src/main/resources/wsdl/librarySchema.xsd` defines the entities, requests,
responses and field validation. Maven generates the matching Java classes under
`target/generated-sources/jaxb/`; edit the XSD rather than the generated classes.

The XSD checks required fields, nonblank text, lengths, integer values and a
publishing year of at least 1900. Services check rules that need the database:
referenced authors/companies must exist, and referenced entities cannot be deleted.

`library.wsdl` describes all 14 operations and their declared faults.
Update/delete operations return a boolean success acknowledgement.

## Postman

Import `../postman/soapapi.postman_collection.json` and send the numbered
requests in order, or use Collection Runner. The create requests save generated
IDs automatically. The collection checks all operations and fault types, then
deletes the example records it created. If interrupted, requests 24–26 clean up
those records; delete the book first.

All requests use the same POST URL. The XML request element selects the operation.
Use `Content-Type: text/xml; charset=utf-8`; the collection includes this header
and the SOAPAction for each request. Keep XML fields in the order shown.

Expected SOAP faults use HTTP 500. Read the detail element to distinguish
`NotFoundFault`, `ValidationFault` and `ConflictFault`.
