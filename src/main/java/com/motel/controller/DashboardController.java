package com.motel.controller;

import com.motel.dto.DashboardResponse;
import com.motel.service.DashboardService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Get
    public DashboardResponse getDashboard() {
        return dashboardService.getDashboard();
    }
}
