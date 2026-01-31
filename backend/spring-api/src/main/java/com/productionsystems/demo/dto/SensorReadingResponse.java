package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class SensorReadingResponse {
    OffsetDateTime timestamp;
    double humidityPct;
    double temperatureC;
    String rawLine;
}
