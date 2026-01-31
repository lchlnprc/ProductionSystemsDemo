package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class DeviceResponse {
    UUID id;
    String deviceId;
    OffsetDateTime registeredAt;
}
