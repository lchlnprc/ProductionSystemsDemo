package com.productionsystems.demo.controller;

import com.productionsystems.demo.dto.LatestDashboardResponse;
import com.productionsystems.demo.dto.MetricsResponse;
import com.productionsystems.demo.dto.TestRunDetailResponse;
import com.productionsystems.demo.dto.TestRunResponse;
import com.productionsystems.demo.dto.TestRunSubmissionRequest;
import com.productionsystems.demo.service.TestRunService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestRunController {
    private final TestRunService testRunService;

    @PostMapping("/test-runs")
    @ResponseStatus(HttpStatus.CREATED)
    public TestRunResponse submit(@Valid @RequestBody TestRunSubmissionRequest request) {
        return testRunService.submit(request);
    }

    @GetMapping("/test-runs")
    public List<TestRunResponse> list(@RequestParam(value = "deviceId", required = false) String deviceId) {
        return testRunService.list(deviceId);
    }

    @GetMapping("/test-runs/latest")
    public LatestDashboardResponse latest() {
        return testRunService.latest();
    }

    @GetMapping("/test-runs/{id}")
    public TestRunDetailResponse get(@PathVariable("id") UUID id) {
        return testRunService.getById(id);
    }

    @GetMapping("/metrics")
    public MetricsResponse metrics() {
        return testRunService.metrics();
    }
}
