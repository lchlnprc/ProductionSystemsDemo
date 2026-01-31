package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TestResultDetailResponse {
    String name;
    String status;
    String message;
    Integer durationMs;
    List<SensorReadingResponse> readings;
}
