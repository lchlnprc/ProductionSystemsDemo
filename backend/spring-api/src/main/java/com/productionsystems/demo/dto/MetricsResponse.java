package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MetricsResponse {
    long totalTestRuns;
    long passedRuns;
    long failedRuns;
    long devicesRegistered;
}
