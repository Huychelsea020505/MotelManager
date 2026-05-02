package com.motel.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

@Serdeable
public class CreateInvoiceRequest {
    @NotNull
    private Long roomId;

    @NotNull
    private Long tenantId;

    @NotBlank
    @Pattern(regexp = "\\d{2}/\\d{4}", message = "Month must use MM/yyyy format")
    private String month;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal roomPrice;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal waterPrice;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal electricityPrice;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal servicePrice;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(BigDecimal roomPrice) {
        this.roomPrice = roomPrice;
    }

    public BigDecimal getWaterPrice() {
        return waterPrice;
    }

    public void setWaterPrice(BigDecimal waterPrice) {
        this.waterPrice = waterPrice;
    }

    public BigDecimal getElectricityPrice() {
        return electricityPrice;
    }

    public void setElectricityPrice(BigDecimal electricityPrice) {
        this.electricityPrice = electricityPrice;
    }

    public BigDecimal getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(BigDecimal servicePrice) {
        this.servicePrice = servicePrice;
    }
}
