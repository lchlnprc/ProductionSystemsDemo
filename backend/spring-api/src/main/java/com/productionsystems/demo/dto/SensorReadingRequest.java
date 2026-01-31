package com.productionsystems.demo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SensorReadingRequest {
    @NotNull
    private OffsetDateTime timestamp;

    @NotNull
    private Double humidityPct;

    @NotNull
    private Double temperatureC;

    private String rawLine;
}
