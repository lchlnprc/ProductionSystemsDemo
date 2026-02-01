package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class TestExecutionResponse {
    UUID id;
    String deviceId;
    String status;
    OffsetDateTime requestedAt;
    OffsetDateTime startedAt;
    OffsetDateTime finishedAt;
    String message;
}
