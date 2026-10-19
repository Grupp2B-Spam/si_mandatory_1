package assignment.books;

import assignment.books.proto.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class BookClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) { usage(); return; }
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext().intercept(ClientIdentity.forClient(args[0])).build();
        Runtime.getRuntime().addShutdownHook(new Thread(channel::shutdownNow));
        try {
            var blocking = BookServiceGrpc.newBlockingStub(channel).withDeadlineAfter(5, TimeUnit.SECONDS);
            switch (args[1]) {
                case "create" -> {
                    if (args.length != 6) { usage(); return; }
                    Book book = blocking.createBook(CreateBookRequest.newBuilder()
                            .setName(args[2]).setAuthorId(args[3]).setPublisherId(args[4])
                            .setPublicationYear(Integer.parseInt(args[5])).build()).getBook();
                    System.out.println("Created:\n" + book);
                }
                case "get" -> {
                    if (args.length != 3) { usage(); return; }
                    System.out.println(blocking.getBookById(GetBookByIdRequest.newBuilder().setId(args[2]).build()));
                }
                case "watch" -> {
                    CountDownLatch done = new CountDownLatch(1);
                    BookServiceGrpc.newStub(channel).watchBooks(WatchBooksRequest.getDefaultInstance(),
                            new StreamObserver<>() {
                                public void onNext(Book book) { System.out.println("New book:\n" + book); }
                                public void onError(Throwable error) {
                                    System.err.println(Status.fromThrowable(error)); done.countDown();
                                }
                                public void onCompleted() { done.countDown(); }
                            });
                    System.out.println("Watch requested as " + args[0] + "; leave running. Ctrl+C to stop.");
                    done.await();
                }
                default -> usage();
            }
        } catch (StatusRuntimeException e) {
            System.err.println(e.getStatus());
            throw e;
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static void usage() {
        System.out.println("Commands:\n  <client-id> watch\n  <client-id> create <name> <author-id> <publisher-id> <year>\n  <client-id> get <book-id>");
    }
}
