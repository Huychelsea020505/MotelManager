package com.motel.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.motel.domain.Invoice;
import com.motel.domain.InvoiceStatus;
import com.motel.domain.Payment;
import com.motel.domain.Room;
import com.motel.domain.RoomStatus;
import com.motel.domain.Tenant;
import com.motel.domain.User;
import com.motel.repository.InvoiceRepository;
import com.motel.repository.PaymentRepository;
import com.motel.repository.RoomRepository;
import com.motel.repository.TenantRepository;
import com.motel.repository.UserRepository;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class DemoDataInitializer implements ApplicationEventListener<ServerStartupEvent> {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public DemoDataInitializer(UserRepository userRepository, RoomRepository roomRepository, TenantRepository tenantRepository, InvoiceRepository invoiceRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ServerStartupEvent event) {
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setFullName("Motel Manager");
        userRepository.save(admin);

        User huy = new User();
        huy.setUsername("huy");
        huy.setPassword("123456");
        huy.setFullName("Huy Nguyen");
        userRepository.save(huy);

        Room a101 = createRoom("A101", "2500000", 20.0, "15000", RoomStatus.OCCUPIED);
        Room a102 = createRoom("A102", "2300000", 18.0, "15000", RoomStatus.AVAILABLE);
        Room b201 = createRoom("B201", "3000000", 25.0, "18000", RoomStatus.OCCUPIED);
        roomRepository.save(a102);
        a101 = roomRepository.save(a101);
        b201 = roomRepository.save(b201);

        Tenant an = createTenant("001203000001", "Nguyen Van An", LocalDate.of(1998, 5, 12), LocalDate.of(2026, 1, 5), a101);
        Tenant binh = createTenant("001204000002", "Tran Thi Binh", LocalDate.of(2000, 9, 21), LocalDate.of(2026, 2, 10), b201);
        an = tenantRepository.save(an);
        binh = tenantRepository.save(binh);

        Invoice unpaid = createInvoice(a101, an, "05/2026", "2500000", "120000", "300000", "100000", InvoiceStatus.UNPAID);
        invoiceRepository.save(unpaid);

        Invoice paid = createInvoice(b201, binh, "04/2026", "3000000", "150000", "350000", "120000", InvoiceStatus.PAID);
        paid.setPaidAt(LocalDateTime.now().minusDays(10));
        paid = invoiceRepository.save(paid);

        Payment payment = new Payment();
        payment.setInvoice(paid);
        payment.setAmount(paid.getTotalAmount());
        payment.setPaidAt(paid.getPaidAt());
        payment.setNote("Demo paid invoice");
        paymentRepository.save(payment);
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

    private Invoice createInvoice(Room room, Tenant tenant, String month, String roomPrice, String waterPrice, String electricityPrice, String servicePrice, InvoiceStatus status) {
        Invoice invoice = new Invoice();
        invoice.setRoom(room);
        invoice.setTenant(tenant);
        invoice.setMonth(month);
        invoice.setRoomPrice(new BigDecimal(roomPrice));
        invoice.setWaterPrice(new BigDecimal(waterPrice));
        invoice.setElectricityPrice(new BigDecimal(electricityPrice));
        invoice.setServicePrice(new BigDecimal(servicePrice));
        invoice.setTotalAmount(invoice.getRoomPrice()
                .add(invoice.getWaterPrice())
                .add(invoice.getElectricityPrice())
                .add(invoice.getServicePrice()));
        invoice.setStatus(status);
        invoice.setCreatedAt(LocalDateTime.now());
        return invoice;
    }
}
