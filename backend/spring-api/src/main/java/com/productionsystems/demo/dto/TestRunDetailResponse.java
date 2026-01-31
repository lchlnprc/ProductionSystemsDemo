package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class TestRunDetailResponse {
    UUID id;
    String runId;
    String deviceId;
    OffsetDateTime startedAt;
    OffsetDateTime finishedAt;
    int totalTests;
    int passed;
    int failed;
    List<TestResultDetailResponse> results;
}
