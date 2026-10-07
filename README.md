# SI Mandatory 1 - Library APIs

Fire APIs til library databasen: REST, SOAP, GraphQL og gRPC. Hver API har sin egen mappe og kan laves i hvilket framework man vil.

## Database

Alle APIs bruger den samme Postgres database.

```
docker compose up -d
```

- Host: `localhost:5432`
- Database / user / password: `library` / `library` / `library`
- Schema og data ligger i `db/init.sql` (migreret fra library.db)

init.sql kører kun første gang. Hvis den bliver ændret skal databasen nulstilles med `docker compose down -v`.

Ændr ikke i init.sql uden at sige det til gruppen.

## APIs

| API     | Mappe      | Port | Framework |
|---------|------------|------|-----------|
| GraphQL | `graphql/` | 8080 | Quarkus   |
| REST    | `rest/`    | 8081 |           |
| SOAP    | `soap/`    | 8082 |           |
| gRPC    | `grpc/`    | 9090 |           |

### GraphQL

```
cd graphql
./mvnw quarkus:dev
```

Endpoint: `POST http://localhost:8080/graphql`
GraphQL UI: http://localhost:8080/q/graphql-ui

## Postman

Collections til alle fire APIs skal ligge i `postman/`.
