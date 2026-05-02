package com.motel.service;

import com.motel.domain.InvoiceStatus;
import com.motel.domain.Room;
import com.motel.domain.RoomStatus;
import com.motel.domain.Tenant;
import com.motel.dto.CreateTenantRequest;
import com.motel.repository.InvoiceRepository;
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
    private final InvoiceRepository invoiceRepository;

    public TenantService(TenantRepository tenantRepository, RoomRepository roomRepository, InvoiceRepository invoiceRepository) {
        this.tenantRepository = tenantRepository;
        this.roomRepository = roomRepository;
        this.invoiceRepository = invoiceRepository;
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

        Room room = findRoomForTenant(request.getRoomId(), null);
        Tenant tenant = new Tenant();
        applyRequest(tenant, request, room);
        markRoomOccupied(room);
        roomRepository.update(room);
        return tenantRepository.save(tenant);
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
        Room newRoom = findRoomForTenant(request.getRoomId(), currentRoomId);
        Room oldRoom = tenant.getRoom();
        applyRequest(tenant, request, newRoom);

        if (!currentRoomId.equals(newRoom.getId())) {
            markRoomAvailable(oldRoom);
            markRoomOccupied(newRoom);
            roomRepository.update(oldRoom);
            roomRepository.update(newRoom);
        }

        return tenantRepository.update(tenant);
    }

    @Transactional
    public void delete(Long id) {
        Tenant tenant = findById(id);
        if (invoiceRepository.existsByTenantIdAndStatus(id, InvoiceStatus.UNPAID)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Cannot delete tenant with unpaid invoices");
        }

        Room room = tenant.getRoom();
        tenantRepository.delete(tenant);
        markRoomAvailable(room);
        roomRepository.update(room);
    }

    private Room findRoomForTenant(Long roomId, Long currentRoomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        boolean sameRoom = currentRoomId != null && currentRoomId.equals(room.getId());
        if (!sameRoom && room.getStatus() == RoomStatus.OCCUPIED) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Room is already occupied");
        }
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
