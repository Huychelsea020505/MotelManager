package com.motel.service;

import com.motel.domain.Invoice;
import com.motel.domain.InvoiceStatus;
import com.motel.dto.DashboardResponse;
import com.motel.grpc.RoomGrpcClient;
import com.motel.repository.InvoiceRepository;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

@Singleton
public class DashboardService {
    private final InvoiceRepository invoiceRepository;
    private final RoomGrpcClient roomGrpcClient;

    public DashboardService(InvoiceRepository invoiceRepository, RoomGrpcClient roomGrpcClient) {
        this.invoiceRepository = invoiceRepository;
        this.roomGrpcClient = roomGrpcClient;
    }

    public DashboardResponse getDashboard() {
        long availableRooms = roomGrpcClient.countAvailableRooms();
        List<Invoice> unpaidInvoices = invoiceRepository.findByStatus(InvoiceStatus.UNPAID);
        BigDecimal totalRevenue = invoiceRepository.findByStatus(InvoiceStatus.PAID).stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponse(0, availableRooms, 0, totalRevenue, unpaidInvoices);
    }
}
