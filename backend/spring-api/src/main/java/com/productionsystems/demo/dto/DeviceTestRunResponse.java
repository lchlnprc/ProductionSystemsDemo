package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class DeviceTestRunResponse {
    UUID id;
    String runId;
    String status;
    OffsetDateTime finishedAt;
}
