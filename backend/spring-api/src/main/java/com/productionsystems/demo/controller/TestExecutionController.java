package com.productionsystems.demo.controller;

import com.productionsystems.demo.dto.TestExecutionRequest;
import com.productionsystems.demo.dto.TestExecutionResponse;
import com.productionsystems.demo.dto.TestExecutionUpdateRequest;
import com.productionsystems.demo.service.TestExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestExecutionController {
    private final TestExecutionService testExecutionService;

    @PostMapping("/devices/{deviceId}/run-test")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TestExecutionResponse runTest(
            @Parameter(description = "Device identifier")
            @PathVariable("deviceId") String deviceId,
            @Valid @RequestBody(required = false) TestExecutionRequest request
    ) {
        return testExecutionService.trigger(deviceId, request);
    }

    @GetMapping("/test-executions/{id}")
    public TestExecutionResponse get(
            @Parameter(description = "Test execution id")
            @PathVariable("id") UUID id
    ) {
        return testExecutionService.get(id);
    }

    @GetMapping("/test-executions/next")
    public TestExecutionResponse next(
            @Parameter(description = "Optional device id")
            @RequestParam(value = "deviceId", required = false) String deviceId
    ) {
        return testExecutionService.claimNext(deviceId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NO_CONTENT));
    }

    @PatchMapping("/test-executions/{id}")
    public TestExecutionResponse update(
            @Parameter(description = "Test execution id")
            @PathVariable("id") UUID id,
            @Valid @RequestBody TestExecutionUpdateRequest request
    ) {
        return testExecutionService.update(id, request);
    }
}
