package assignment.books;

import assignment.books.proto.*;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import java.util.*;

public final class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {
    // A single lock protects storage, subscriptions, and writes to stream observers.
    // StreamObserver is not thread-safe; concurrent creates must not write together.
    private final Map<String, Book> books = new HashMap<>();
    private final Set<Subscription> subscribers = new HashSet<>();
    private static final int MAX_PENDING = 256;

    private static final class Subscription {
        final String clientId;
        final ServerCallStreamObserver<Book> observer;
        final Queue<Book> pending = new ArrayDeque<>();
        Subscription(String clientId, ServerCallStreamObserver<Book> observer) {
            this.clientId = clientId;
            this.observer = observer;
        }
    }

    @Override
    public synchronized void getBookById(GetBookByIdRequest request, StreamObserver<Book> response) {
        if (request.getId().isBlank()) {
            response.onError(Status.INVALID_ARGUMENT.withDescription("id is required").asRuntimeException());
            return;
        }
        Book book = books.get(request.getId());
        if (book == null) {
            response.onError(Status.NOT_FOUND.withDescription("Book not found").asRuntimeException());
            return;
        }
        response.onNext(book);
        response.onCompleted();
    }

    @Override
    public synchronized void createBook(CreateBookRequest request, StreamObserver<CreateBookResponse> response) {
        if (request.getName().isBlank() || request.getAuthorId().isBlank()
                || request.getPublisherId().isBlank() || request.getPublicationYear() < 1
                || request.getPublicationYear() > 9999) {
            response.onError(Status.INVALID_ARGUMENT.withDescription(
                    "name, author_id, publisher_id and a year from 1 to 9999 are required").asRuntimeException());
            return;
        }
        Book book = Book.newBuilder().setId(UUID.randomUUID().toString())
                .setName(request.getName()).setAuthorId(request.getAuthorId())
                .setPublisherId(request.getPublisherId())
                .setPublicationYear(request.getPublicationYear()).build();
        books.put(book.getId(), book);
        String creatorId = ClientIdentity.CURRENT.get();
        for (Subscription subscriber : List.copyOf(subscribers)) {
            if (subscriber.observer.isCancelled()) {
                remove(subscriber);
            } else if (!subscriber.clientId.equals(creatorId)) {
                if (subscriber.pending.size() >= MAX_PENDING) {
                    remove(subscriber);
                    subscriber.observer.onError(Status.RESOURCE_EXHAUSTED
                            .withDescription("Subscriber is too slow; reconnect to watch future books")
                            .asRuntimeException());
                } else {
                    subscriber.pending.add(book);
                    drain(subscriber);
                }
            }
        }
        response.onNext(CreateBookResponse.newBuilder().setBook(book).build());
        response.onCompleted();
    }

    @Override
    public synchronized void watchBooks(WatchBooksRequest request, StreamObserver<Book> response) {
        ServerCallStreamObserver<Book> stream = (ServerCallStreamObserver<Book>) response;
        Subscription subscriber = new Subscription(ClientIdentity.CURRENT.get(), stream);
        stream.setOnCancelHandler(() -> remove(subscriber));
        stream.setOnReadyHandler(() -> drain(subscriber));
        subscribers.add(subscriber);
        if (stream.isCancelled()) remove(subscriber);
        // No onCompleted here: keep this RPC open for future book creations.
    }

    private synchronized void drain(Subscription subscriber) {
        if (!subscribers.contains(subscriber)) return;
        while (!subscriber.pending.isEmpty() && subscriber.observer.isReady()) {
            if (subscriber.observer.isCancelled()) {
                remove(subscriber);
                return;
            }
            subscriber.observer.onNext(subscriber.pending.remove());
        }
    }

    private synchronized void remove(Subscription subscriber) {
        subscribers.remove(subscriber);
        subscriber.pending.clear();
    }

    // Package-private diagnostic used by the in-process integration tests.
    synchronized int subscriptionCount() { return subscribers.size(); }
}
