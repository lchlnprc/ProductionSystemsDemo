package com.productionsystems.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TestResultRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String status;

    private String message;

    @NotNull
    private OffsetDateTime startedAt;

    private OffsetDateTime finishedAt;

    private Integer durationMs;

    private List<SensorReadingRequest> readings = new ArrayList<>();
}
