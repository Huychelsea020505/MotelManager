package com.motel.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import jakarta.annotation.PreDestroy;
import java.io.IOException;

@Context
public class RoomGrpcServer {
    private final Server server;

    public RoomGrpcServer(
            RoomGrpcEndpoint roomGrpcEndpoint,
            @Value("${grpc.server.port}") int port
    ) throws IOException {
        this.server = ServerBuilder.forPort(port)
                .addService(roomGrpcEndpoint)
                .build()
                .start();
    }

    @PreDestroy
    void stop() {
        server.shutdown();
    }
}
