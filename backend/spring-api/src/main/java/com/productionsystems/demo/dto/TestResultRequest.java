package com.productionsystems.demo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
