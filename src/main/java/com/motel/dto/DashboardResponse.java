package com.motel.dto;

import com.motel.domain.Invoice;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.util.List;

@Serdeable
public class DashboardResponse {
    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private BigDecimal totalRevenue;
    private List<Invoice> unpaidInvoices;

    public DashboardResponse() {
    }

    public DashboardResponse(long totalRooms, long availableRooms, long occupiedRooms, BigDecimal totalRevenue, List<Invoice> unpaidInvoices) {
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.occupiedRooms = occupiedRooms;
        this.totalRevenue = totalRevenue;
        this.unpaidInvoices = unpaidInvoices;
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(long availableRooms) {
        this.availableRooms = availableRooms;
    }

    public long getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public List<Invoice> getUnpaidInvoices() {
        return unpaidInvoices;
    }

    public void setUnpaidInvoices(List<Invoice> unpaidInvoices) {
        this.unpaidInvoices = unpaidInvoices;
    }
}
