package com.motel.controller;

import com.motel.domain.Payment;
import com.motel.service.InvoiceService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import java.util.List;

@Controller("/payments")
public class PaymentController {
    private final InvoiceService invoiceService;

    public PaymentController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Get("/history")
    public List<Payment> history() {
        return invoiceService.paymentHistory();
    }
}
