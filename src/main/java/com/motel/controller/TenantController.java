package com.motel.controller;

import com.motel.domain.Tenant;
import com.motel.dto.CreateTenantRequest;
import com.motel.service.TenantService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import jakarta.validation.Valid;
import java.util.List;

@Controller("/tenants")
public class TenantController {
    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Get
    public List<Tenant> findAll() {
        return tenantService.findAll();
    }

    @Post
    public Tenant create(@Body @Valid CreateTenantRequest request) {
        return tenantService.create(request);
    }

    @Get("/{id}")
    public Tenant findById(Long id) {
        return tenantService.findById(id);
    }

    @Put("/{id}")
    public Tenant update(Long id, @Body @Valid CreateTenantRequest request) {
        return tenantService.update(id, request);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(Long id) {
        tenantService.delete(id);
    }
}
