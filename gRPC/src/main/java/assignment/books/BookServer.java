package assignment.books;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.concurrent.TimeUnit;

public final class BookServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
                .addService(new BookServiceImpl()).intercept(new ClientIdentity()).build().start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            try {
                if (!server.awaitTermination(3, TimeUnit.SECONDS)) server.shutdownNow();
            } catch (InterruptedException e) {
                server.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));
        System.out.println("Book server listening on port 50051");
        server.awaitTermination();
    }
}
