package edu.backend.infrastructure.in.web;

import edu.backend.application.service.ManagementApplicationService;
import edu.backend.infrastructure.in.web.dto.DashboardResponse;
import edu.backend.infrastructure.out.security.security.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager")
public class ManagementController {

    private final ManagementApplicationService managementApplicationService;

    public ManagementController(ManagementApplicationService managementApplicationService) {
        this.managementApplicationService = managementApplicationService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard(@AuthenticationPrincipal CurrentUser currentUser) {
        return DashboardResponse.from(
                managementApplicationService.getDashboard(currentUser.toDomainUser())
        );
    }
}