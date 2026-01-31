package com.productionsystems.demo.controller;

import com.productionsystems.demo.dto.DeviceRegistrationRequest;
import com.productionsystems.demo.dto.DeviceResponse;
import com.productionsystems.demo.dto.DeviceTestRunResponse;
import com.productionsystems.demo.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceResponse register(@Valid @RequestBody DeviceRegistrationRequest request) {
        return deviceService.register(request);
    }

    @GetMapping
    public List<DeviceResponse> list() {
        return deviceService.list();
    }

    @GetMapping("/{id}/test-runs")
    public List<DeviceTestRunResponse> testRuns(@PathVariable("id") UUID id) {
        return deviceService.testRuns(id);
    }
}
