package com.motel.service;

import com.motel.domain.Room;
import com.motel.domain.RoomStatus;
import com.motel.domain.Tenant;
import com.motel.dto.CreateTenantRequest;
import com.motel.repository.RoomRepository;
import com.motel.repository.TenantRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class TenantService {
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;

    public TenantService(TenantRepository tenantRepository, RoomRepository roomRepository) {
        this.tenantRepository = tenantRepository;
        this.roomRepository = roomRepository;
    }

    public List<Tenant> findAll() {
        List<Tenant> tenants = new ArrayList<>();
        tenantRepository.findAll().forEach(tenants::add);
        return tenants;
    }

    public Tenant findById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
    }

    @Transactional
    public Tenant create(CreateTenantRequest request) {
        if (tenantRepository.existsByCitizenId(request.getCitizenId())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Citizen ID already exists");
        }

        Room room = findRoomForTenant(request.getRoomId());
        Tenant tenant = new Tenant();
        applyRequest(tenant, request, room);
        
        tenant = tenantRepository.save(tenant);

        if (room.getStatus() == RoomStatus.AVAILABLE) {
            markRoomOccupied(room);
            roomRepository.update(room);
        }
        
        return tenant;
    }

    @Transactional
    public Tenant update(Long id, CreateTenantRequest request) {
        Tenant tenant = findById(id);
        tenantRepository.findByCitizenId(request.getCitizenId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Citizen ID already exists");
                });

        Long currentRoomId = tenant.getRoom().getId();
        Room newRoom = findRoomForTenant(request.getRoomId());
        Room oldRoom = tenant.getRoom();
        
        applyRequest(tenant, request, newRoom);
        tenant = tenantRepository.update(tenant);

        if (!currentRoomId.equals(newRoom.getId())) {
            // Check if old room has any tenants left
            long remainingTenants = tenantRepository.findByRoomId(oldRoom.getId()).stream()
                    .filter(t -> !t.getId().equals(id))
                    .count();
            if (remainingTenants == 0) {
                markRoomAvailable(oldRoom);
                roomRepository.update(oldRoom);
            }

            // Update new room status
            if (newRoom.getStatus() == RoomStatus.AVAILABLE) {
                markRoomOccupied(newRoom);
                roomRepository.update(newRoom);
            }
        }

        return tenant;
    }

    @Transactional
    public void delete(Long id) {
        Tenant tenant = findById(id);
        Room room = tenant.getRoom();
        tenantRepository.delete(tenant);
        
        // Only mark the room as available if this was the last tenant
        long remainingTenants = tenantRepository.findByRoomId(room.getId()).stream()
                .filter(t -> !t.getId().equals(id))
                .count();
        if (remainingTenants == 0) {
            markRoomAvailable(room);
            roomRepository.update(room);
        }
    }

    private Room findRoomForTenant(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Room is under maintenance");
        }
        return room;
    }

    private void applyRequest(Tenant tenant, CreateTenantRequest request, Room room) {
        tenant.setCitizenId(request.getCitizenId());
        tenant.setFullName(request.getFullName());
        tenant.setBirthDate(request.getBirthDate());
        tenant.setMoveInDate(request.getMoveInDate());
        tenant.setRoom(room);
    }

    private void markRoomOccupied(Room room) {
        room.setOccupied(true);
        room.setStatus(RoomStatus.OCCUPIED);
    }

    private void markRoomAvailable(Room room) {
        room.setOccupied(false);
        room.setStatus(RoomStatus.AVAILABLE);
    }
}
