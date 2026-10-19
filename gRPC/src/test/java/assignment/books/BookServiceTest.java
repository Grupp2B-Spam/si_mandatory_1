package assignment.books;

import assignment.books.proto.*;
import io.grpc.*;
import io.grpc.inprocess.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    private Server server;
    private ManagedChannel channel;
    private BookServiceImpl service;

    @BeforeEach void start() throws Exception {
        String name = InProcessServerBuilder.generateName();
        service = new BookServiceImpl();
        server = InProcessServerBuilder.forName(name).directExecutor()
                .addService(service).intercept(new ClientIdentity()).build().start();
        channel = InProcessChannelBuilder.forName(name).directExecutor().build();
    }
    @AfterEach void stop() throws Exception {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        server.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
    private BookServiceGrpc.BookServiceBlockingStub client(String id) {
        return BookServiceGrpc.newBlockingStub(channel).withInterceptors(ClientIdentity.forClient(id))
                .withDeadlineAfter(3, TimeUnit.SECONDS);
    }
    private CreateBookRequest request(String title) {
        return CreateBookRequest.newBuilder().setName(title).setAuthorId("a1")
                .setPublisherId("p1").setPublicationYear(2024).build();
    }
    private Context.CancellableContext watch(String id, BlockingQueue<Book> received) {
        var context = Context.current().withCancellation();
        context.run(() -> BookServiceGrpc.newStub(channel).withInterceptors(ClientIdentity.forClient(id))
                .watchBooks(WatchBooksRequest.getDefaultInstance(), new StreamObserver<>() {
                    public void onNext(Book value) { received.add(value); }
                    public void onError(Throwable error) {}
                    public void onCompleted() {}
                }));
        return context;
    }

    @Test void createAndGetRoundTrip() {
        Book book = client("alice").createBook(request("Dune")).getBook();
        assertFalse(book.getId().isBlank());
        assertEquals("Dune", book.getName());
        assertEquals("a1", book.getAuthorId());
        assertEquals("p1", book.getPublisherId());
        assertEquals(2024, book.getPublicationYear());
        assertEquals(book, client("bob").getBookById(
                GetBookByIdRequest.newBuilder().setId(book.getId()).build()));
    }
    @Test void invalidRequestsAndMissingBooksHaveGrpcStatuses() {
        assertEquals(Status.Code.INVALID_ARGUMENT, assertThrows(StatusRuntimeException.class,
                () -> client("alice").createBook(CreateBookRequest.getDefaultInstance())).getStatus().getCode());
        assertEquals(Status.Code.NOT_FOUND, assertThrows(StatusRuntimeException.class,
                () -> client("alice").getBookById(GetBookByIdRequest.newBuilder().setId("missing").build()))
                .getStatus().getCode());
        assertEquals(Status.Code.INVALID_ARGUMENT, assertThrows(StatusRuntimeException.class,
                () -> client("alice").getBookById(GetBookByIdRequest.getDefaultInstance())).getStatus().getCode());
        assertEquals(Status.Code.INVALID_ARGUMENT, assertThrows(StatusRuntimeException.class,
                () -> BookServiceGrpc.newBlockingStub(channel).createBook(request("No identity")))
                .getStatus().getCode());
    }
    @Test void broadcastsToOthersOnlyAndRemovesCancelledSubscriptions() throws Exception {
        client("alice").createBook(request("Before watching"));
        var alice = new LinkedBlockingQueue<Book>();
        var bob = new LinkedBlockingQueue<Book>();
        var carol = new LinkedBlockingQueue<Book>();
        try (var a = watch("alice", alice); var b = watch("bob", bob); var c = watch("carol", carol)) {
            assertEquals(3, service.subscriptionCount());
            assertTrue(bob.isEmpty(), "No history replay");
            Book first = client("alice").createBook(request("First")).getBook();
            assertEquals(first, bob.poll(2, TimeUnit.SECONDS));
            assertEquals(first, carol.poll(2, TimeUnit.SECONDS));
            assertNull(alice.poll(100, TimeUnit.MILLISECONDS), "Creator must not receive its own event");
            Book second = client("bob").createBook(request("Second")).getBook();
            assertEquals(second, alice.poll(2, TimeUnit.SECONDS));
            assertEquals(second, carol.poll(2, TimeUnit.SECONDS));
            assertTrue(bob.isEmpty());
            b.cancel(null);
            assertEquals(2, service.subscriptionCount());
        }
        assertEquals(0, service.subscriptionCount());
    }
    @Test void concurrentCreatesReachWatcherExactlyOnce() throws Exception {
        var received = new LinkedBlockingQueue<Book>();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try (var watch = watch("reader", received)) {
            List<Future<Book>> futures = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                int index = i;
                futures.add(pool.submit(() -> client("writer").createBook(request("Book " + index)).getBook()));
            }
            Set<String> expected = new HashSet<>();
            for (var future : futures) expected.add(future.get(5, TimeUnit.SECONDS).getId());
            Set<String> actual = new HashSet<>();
            for (int i = 0; i < 40; i++) {
                Book book = received.poll(2, TimeUnit.SECONDS);
                assertNotNull(book);
                assertTrue(actual.add(book.getId()), "No duplicate notifications");
            }
            assertEquals(expected, actual);
            assertTrue(received.isEmpty());
        } finally { pool.shutdownNow(); }
    }
}
