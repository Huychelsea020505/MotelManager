package com.motel.service;

import com.motel.domain.Invoice;
import com.motel.domain.InvoiceStatus;
import com.motel.domain.Payment;
import com.motel.domain.Room;
import com.motel.domain.Tenant;
import com.motel.dto.CreateInvoiceRequest;
import com.motel.dto.PayInvoiceRequest;
import com.motel.repository.InvoiceRepository;
import com.motel.repository.PaymentRepository;
import com.motel.repository.RoomRepository;
import com.motel.repository.TenantRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository, RoomRepository roomRepository, TenantRepository tenantRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
    }

    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        invoiceRepository.findAll().forEach(invoices::add);
        return invoices;
    }

    public List<Invoice> findUnpaid() {
        return invoiceRepository.findByStatus(InvoiceStatus.UNPAID);
    }

    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    public List<Payment> paymentHistory() {
        List<Payment> payments = new ArrayList<>();
        paymentRepository.findAll().forEach(payments::add);
        return payments;
    }

    @Transactional
    public Invoice create(CreateInvoiceRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));

        if (!tenant.getRoom().getId().equals(room.getId())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tenant does not belong to this room");
        }

        Invoice invoice = new Invoice();
        invoice.setRoom(room);
        invoice.setTenant(tenant);
        invoice.setMonth(request.getMonth());
        invoice.setRoomPrice(request.getRoomPrice());
        invoice.setWaterPrice(request.getWaterPrice());
        invoice.setElectricityPrice(request.getElectricityPrice());
        invoice.setServicePrice(request.getServicePrice());
        invoice.setTotalAmount(request.getRoomPrice()
                .add(request.getWaterPrice())
                .add(request.getElectricityPrice())
                .add(request.getServicePrice()));
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setCreatedAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Payment pay(Long invoiceId, PayInvoiceRequest request) {
        Invoice invoice = findById(invoiceId);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invoice is already paid");
        }

        BigDecimal amount = request.getAmount() == null ? invoice.getTotalAmount() : request.getAmount();
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        invoiceRepository.update(invoice);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setPaidAt(invoice.getPaidAt());
        payment.setNote(request.getNote());
        return paymentRepository.save(payment);
    }
}
