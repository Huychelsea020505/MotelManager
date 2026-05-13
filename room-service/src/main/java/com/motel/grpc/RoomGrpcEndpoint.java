package com.motel.grpc;

import com.motel.domain.Room;
import com.motel.domain.RoomStatus;
import com.motel.domain.Tenant;
import com.motel.repository.RoomRepository;
import com.motel.repository.TenantRepository;
import io.grpc.stub.StreamObserver;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class RoomGrpcEndpoint extends RoomGrpcServiceGrpc.RoomGrpcServiceImplBase {
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    public RoomGrpcEndpoint(RoomRepository roomRepository, TenantRepository tenantRepository) {
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public void getRoomById(RoomRequest request, StreamObserver<RoomResponse> responseObserver) {
        RoomResponse response = roomRepository.findById(request.getRoomId())
                .map(this::toResponse)
                .orElse(RoomResponse.newBuilder().setFound(false).build());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void streamAvailableRooms(Empty request, StreamObserver<RoomResponse> responseObserver) {
        roomRepository.findByStatus(RoomStatus.AVAILABLE)
                .forEach(room -> responseObserver.onNext(toResponse(room)));
        responseObserver.onCompleted();
    }

    private RoomResponse toResponse(Room room) {
        List<Tenant> tenants = tenantRepository.findByRoomId(room.getId());
        String tenantName = tenants.isEmpty() ? "" : tenants.get(0).getFullName();

        return RoomResponse.newBuilder()
                .setId(room.getId())
                .setRoomNumber(room.getName())
                .setPrice(room.getPrice().doubleValue())
                .setArea(room.getArea())
                .setStatus(room.getStatus().name())
                .setTenantName(tenantName)
                .setFound(true)
                .build();
    }
}
