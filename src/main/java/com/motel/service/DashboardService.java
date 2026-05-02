package com.motel.service;

import com.motel.domain.Invoice;
import com.motel.domain.InvoiceStatus;
import com.motel.domain.Room;
import com.motel.domain.RoomStatus;
import com.motel.dto.DashboardResponse;
import com.motel.repository.InvoiceRepository;
import com.motel.repository.RoomRepository;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DashboardService {
    private final RoomRepository roomRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardService(RoomRepository roomRepository, InvoiceRepository invoiceRepository) {
        this.roomRepository = roomRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public DashboardResponse getDashboard() {
        List<Room> rooms = new ArrayList<>();
        roomRepository.findAll().forEach(rooms::add);

        long totalRooms = rooms.size();
        long occupiedRooms = rooms.stream().filter(room -> room.getStatus() == RoomStatus.OCCUPIED).count();
        long availableRooms = rooms.stream().filter(room -> room.getStatus() == RoomStatus.AVAILABLE).count();
        List<Invoice> unpaidInvoices = invoiceRepository.findByStatus(InvoiceStatus.UNPAID);
        BigDecimal totalRevenue = invoiceRepository.findByStatus(InvoiceStatus.PAID).stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponse(totalRooms, availableRooms, occupiedRooms, totalRevenue, unpaidInvoices);
    }
}
