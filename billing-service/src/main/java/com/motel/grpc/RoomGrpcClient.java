package com.motel.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import jakarta.annotation.PreDestroy;
import java.util.Iterator;

@Context
public class RoomGrpcClient {
    private final ManagedChannel channel;
    private final RoomGrpcServiceGrpc.RoomGrpcServiceBlockingStub blockingStub;

    public RoomGrpcClient(
            @Value("${room.grpc.host}") String host,
            @Value("${room.grpc.port}") int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = RoomGrpcServiceGrpc.newBlockingStub(channel);
    }

    public RoomResponse getRoomById(Long roomId) {
        RoomRequest request = RoomRequest.newBuilder()
                .setRoomId(roomId)
                .build();
        return blockingStub.getRoomById(request);
    }

    public long countAvailableRooms() {
        Iterator<RoomResponse> responses = blockingStub.streamAvailableRooms(Empty.newBuilder().build());
        long count = 0;
        while (responses.hasNext()) {
            responses.next();
            count++;
        }
        return count;
    }

    @PreDestroy
    void close() {
        channel.shutdown();
    }
}
