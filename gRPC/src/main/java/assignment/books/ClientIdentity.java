package assignment.books;

import io.grpc.*;
import io.grpc.stub.MetadataUtils;

/** Demo client identity: caller-supplied metadata, not authentication. */
public final class ClientIdentity implements ServerInterceptor {
    public static final Metadata.Key<String> HEADER =
            Metadata.Key.of("x-client-id", Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<String> CURRENT = Context.key("client-id");

    public static ClientInterceptor forClient(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("clientId is required");
        }
        Metadata headers = new Metadata();
        headers.put(HEADER, clientId);
        return MetadataUtils.newAttachHeadersInterceptor(headers);
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        String id = headers.get(HEADER);
        if (id == null || id.isBlank()) {
            call.close(Status.INVALID_ARGUMENT.withDescription("x-client-id header is required"),
                    new Metadata());
            return new ServerCall.Listener<>() {};
        }
        return Contexts.interceptCall(Context.current().withValue(CURRENT, id), call, headers, next);
    }
}
