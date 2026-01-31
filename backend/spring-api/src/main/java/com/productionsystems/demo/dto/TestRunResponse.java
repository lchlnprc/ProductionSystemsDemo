package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class TestRunResponse {
    UUID id;
    String runId;
    String deviceId;
    OffsetDateTime startedAt;
    OffsetDateTime finishedAt;
    int totalTests;
    int passed;
    int failed;
}
