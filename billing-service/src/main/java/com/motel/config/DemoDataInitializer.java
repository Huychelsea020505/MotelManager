package com.motel.config;

import com.motel.domain.Invoice;
import com.motel.domain.InvoiceStatus;
import com.motel.domain.Payment;
import com.motel.domain.User;
import com.motel.repository.InvoiceRepository;
import com.motel.repository.PaymentRepository;
import com.motel.repository.UserRepository;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Singleton
public class DemoDataInitializer implements ApplicationEventListener<ServerStartupEvent> {
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public DemoDataInitializer(UserRepository userRepository, InvoiceRepository invoiceRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
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

        Invoice unpaid = createInvoice(1L, 1L, "A101", "Nguyen Van An", "05/2026", "2500000", "120000", "300000", "100000", InvoiceStatus.UNPAID);
        invoiceRepository.save(unpaid);

        Invoice paid = createInvoice(3L, 2L, "B201", "Tran Thi Binh", "04/2026", "3000000", "150000", "350000", "120000", InvoiceStatus.PAID);
        paid.setPaidAt(LocalDateTime.now().minusDays(10));
        paid = invoiceRepository.save(paid);

        Payment payment = new Payment();
        payment.setInvoice(paid);
        payment.setAmount(paid.getTotalAmount());
        payment.setPaidAt(paid.getPaidAt());
        payment.setNote("Demo paid invoice");
        paymentRepository.save(payment);
    }

    private Invoice createInvoice(Long roomId, Long tenantId, String roomName, String tenantName, String month, String roomPrice, String waterPrice, String electricityPrice, String servicePrice, InvoiceStatus status) {
        Invoice invoice = new Invoice();
        invoice.setRoomId(roomId);
        invoice.setTenantId(tenantId);
        invoice.setRoomName(roomName);
        invoice.setTenantName(tenantName);
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
