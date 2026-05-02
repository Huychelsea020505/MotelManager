package com.motel.controller;

import com.motel.domain.Invoice;
import com.motel.domain.Payment;
import com.motel.dto.CreateInvoiceRequest;
import com.motel.dto.PayInvoiceRequest;
import com.motel.service.InvoiceService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import jakarta.validation.Valid;
import java.util.List;

@Controller
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Get("/invoices")
    public List<Invoice> findAll() {
        return invoiceService.findAll();
    }

    @Post("/invoices")
    public Invoice create(@Body @Valid CreateInvoiceRequest request) {
        return invoiceService.create(request);
    }

    @Get("/invoices/unpaid")
    public List<Invoice> findUnpaid() {
        return invoiceService.findUnpaid();
    }

    @Put("/invoices/{id}/pay")
    public Payment pay(Long id, @Body @Valid PayInvoiceRequest request) {
        return invoiceService.pay(id, request);
    }
}
