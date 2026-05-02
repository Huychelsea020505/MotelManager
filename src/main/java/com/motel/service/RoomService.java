package com.motel.service;

import com.motel.domain.Room;
import com.motel.domain.RoomStatus;
import com.motel.dto.CreateRoomRequest;
import com.motel.repository.RoomRepository;
import com.motel.repository.TenantRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class RoomService {
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    public RoomService(RoomRepository roomRepository, TenantRepository tenantRepository) {
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        roomRepository.findAll().forEach(rooms::add);
        return rooms;
    }

    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    public List<Room> findOccupiedRooms() {
        return roomRepository.findByStatus(RoomStatus.OCCUPIED);
    }

    @Transactional
    public Room create(CreateRoomRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Room name already exists");
        }

        Room room = new Room();
        applyRequest(room, request);
        return roomRepository.save(room);
    }

    @Transactional
    public Room update(Long id, CreateRoomRequest request) {
        Room room = findById(id);
        roomRepository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Room name already exists");
                });

        applyRequest(room, request);
        return roomRepository.update(room);
    }

    @Transactional
    public void delete(Long id) {
        Room room = findById(id);
        if (!tenantRepository.findByRoomId(id).isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Cannot delete an occupied room");
        }
        roomRepository.delete(room);
    }

    private void applyRequest(Room room, CreateRoomRequest request) {
        RoomStatus status = request.getStatus() == null ? RoomStatus.AVAILABLE : request.getStatus();
        room.setName(request.getName());
        room.setPrice(request.getPrice());
        room.setArea(request.getArea());
        room.setWaterPrice(request.getWaterPrice());
        room.setStatus(status);
        room.setOccupied(status == RoomStatus.OCCUPIED);
    }
}
