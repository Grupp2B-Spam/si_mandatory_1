# Java gRPC book service

A small assignment project using Java 17+, Maven 3.9+, Protocol Buffers and grpc-java. No Spring or database is needed. Versions are pinned for a reproducible example; they are not a claim about the newest releases.

## Build and test

From this directory:

```sh
mvn test
```

Maven downloads dependencies and generates the Java message classes and service stubs from `src/main/proto/books.proto`. Generated source goes under `target/generated-sources/protobuf`; do not write those classes by hand. In IntelliJ, open `pom.xml` as a Maven project and select a JDK 17 or newer.

## Run the demo

Run these commands from the project directory, in separate terminals. Leave the server and watcher running.

Terminal 1 — server:

```sh
mvn exec:java -Dexec.mainClass=assignment.books.BookServer
```

Terminal 2 — client Alice subscribes:

```sh
mvn exec:java -Dexec.mainClass=assignment.books.BookClient -Dexec.args="alice watch"
```

Terminal 3 — client Bob creates a book:

```sh
mvn exec:java -Dexec.mainClass=assignment.books.BookClient -Dexec.args="bob create 'The Hobbit' author-1 publisher-1 1937"
```

Alice should print `New book:` followed by the book, including its generated UUID. Bob gets the created book as his unary response. Wait for the watcher to connect before creating a book; starting an asynchronous call is not a server acknowledgement of registration.

Copy the printed ID to retrieve the book:

```sh
mvn exec:java -Dexec.mainClass=assignment.books.BookClient -Dexec.args="bob get REPLACE_WITH_BOOK_ID"
```

To demonstrate creator exclusion, create a book using Alice's identity while Alice's watcher remains running:

```sh
mvn exec:java -Dexec.mainClass=assignment.books.BookClient -Dexec.args="alice create 'Dune' author-2 publisher-2 1965"
```

Alice gets the unary result in terminal 3, but her watcher in terminal 2 receives no notification for that book. You can start another watcher with `bob watch` to see it receive Alice's creations. Ctrl+C stops a watcher or server.

## How it works

- `GetBookById` looks up one book and returns `NOT_FOUND` if it does not exist.
- `CreateBook` validates input, generates a UUID, stores the book and notifies other clients' active subscriptions. Its response contains the newly created book.
- `WatchBooks` registers a server-side stream. It deliberately does not call `onCompleted()` during registration. Every later eligible creation calls `onNext(book)` on that stream.
- Each RPC carries an `x-client-id` metadata header. The interceptor places it in gRPC's request context. The service compares the creator's identity with each subscription's identity. This keeps the assignment's message parameters unchanged.
- Reuse one ID across the calls belonging to the same logical client; use different IDs for different clients. Multiple subscriptions with the same ID all exclude creations by that ID. A real client could generate a UUID once per application session.
- A single service lock serializes storage updates and observer writes. This is intentionally simple for the assignment; `StreamObserver` is not thread-safe.
- Cancellation removes the subscription. Flow control checks `isReady()`, temporarily queues up to 256 unsent books per subscriber, and terminates an overloaded stream with `RESOURCE_EXHAUSTED` rather than accumulating unlimited pending events.

## Files worth reading, in order

1. `src/main/proto/books.proto` — wire contract and generated Java package.
2. `src/main/java/assignment/books/BookServiceImpl.java` — the three RPC implementations.
3. `src/main/java/assignment/books/ClientIdentity.java` — client identity metadata.
4. `src/main/java/assignment/books/BookServer.java` — server startup on port 50051.
5. `src/main/java/assignment/books/BookClient.java` — blocking unary calls and asynchronous streaming.
6. `src/test/java/assignment/books/BookServiceTest.java` — in-process gRPC integration tests.

## Assumptions and limits

Storage is in memory: restarting the server loses books. Author and publisher IDs are opaque strings; this example does not maintain author/publisher tables or verify those references. Names and IDs must be nonblank, and the example accepts publication years from 1 to 9999 (including future years). These are implementation choices because the assignment does not specify validation rules.

Notifications are live only, from the point the server registers the subscription. There is no history replay, durable delivery, automatic reconnection or recovery of missed events. Disconnected or overloaded subscribers can miss books. Creating a book is not idempotent: retrying a request can create a second book. The single-process lock is not a distributed synchronization mechanism.

The demo uses plaintext transport and caller-supplied IDs, intended for local testing. The header distinguishes clients but is not authentication. For a deployed service, derive identity from authenticated credentials, use TLS, and add persistent storage if required.

## Test coverage

The integration tests exercise real in-process gRPC calls, rather than mocking observers. They cover create/get, invalid input, missing books, missing identity, delivery to multiple watchers, exclusion of the creator, no history replay, cancellation cleanup, and concurrent creates with unique, nonduplicated notifications. They do not cover TCP/TLS transport or the slow-subscriber overflow path.

## References

- Java basics: https://grpc.io/docs/languages/java/basics/
- Generated-code reference: https://grpc.io/docs/languages/java/generated-code/
- Observer concurrency, cancellation and flow control: https://grpc.github.io/grpc-java/javadoc/io/grpc/stub/ServerCallStreamObserver.html
