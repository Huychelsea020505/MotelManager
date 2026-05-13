package com.motel.config;

import com.motel.domain.Room;
import com.motel.domain.RoomStatus;
import com.motel.domain.Tenant;
import com.motel.repository.RoomRepository;
import com.motel.repository.TenantRepository;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Singleton
public class DemoDataInitializer implements ApplicationEventListener<ServerStartupEvent> {
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    public DemoDataInitializer(RoomRepository roomRepository, TenantRepository tenantRepository) {
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ServerStartupEvent event) {
        if (roomRepository.existsByName("A101")) {
            return;
        }

        Room a101 = roomRepository.save(createRoom("A101", "2500000", 20.0, "15000", RoomStatus.OCCUPIED));
        roomRepository.save(createRoom("A102", "2300000", 18.0, "15000", RoomStatus.AVAILABLE));
        Room b201 = roomRepository.save(createRoom("B201", "3000000", 25.0, "18000", RoomStatus.OCCUPIED));

        tenantRepository.save(createTenant("001203000001", "Nguyen Van An", LocalDate.of(1998, 5, 12), LocalDate.of(2026, 1, 5), a101));
        tenantRepository.save(createTenant("001204000002", "Tran Thi Binh", LocalDate.of(2000, 9, 21), LocalDate.of(2026, 2, 10), b201));
    }

    private Room createRoom(String name, String price, double area, String waterPrice, RoomStatus status) {
        Room room = new Room();
        room.setName(name);
        room.setPrice(new BigDecimal(price));
        room.setArea(area);
        room.setWaterPrice(new BigDecimal(waterPrice));
        room.setStatus(status);
        room.setOccupied(status == RoomStatus.OCCUPIED);
        return room;
    }

    private Tenant createTenant(String citizenId, String fullName, LocalDate birthDate, LocalDate moveInDate, Room room) {
        Tenant tenant = new Tenant();
        tenant.setCitizenId(citizenId);
        tenant.setFullName(fullName);
        tenant.setBirthDate(birthDate);
        tenant.setMoveInDate(moveInDate);
        tenant.setRoom(room);
        return tenant;
    }
}
