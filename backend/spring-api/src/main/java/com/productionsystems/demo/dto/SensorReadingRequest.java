package com.productionsystems.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class SensorReadingRequest {
    @NotNull
    private OffsetDateTime timestamp;

    @NotNull
    private Double humidityPct;

    @NotNull
    private Double temperatureC;

    private String rawLine;
}
