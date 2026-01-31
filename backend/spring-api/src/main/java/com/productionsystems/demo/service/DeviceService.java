package com.productionsystems.demo.service;

import com.productionsystems.demo.domain.Device;
import com.productionsystems.demo.dto.DeviceRegistrationRequest;
import com.productionsystems.demo.dto.DeviceResponse;
import com.productionsystems.demo.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceResponse register(DeviceRegistrationRequest request) {
        Device device = deviceRepository.findByDeviceId(request.getDeviceId())
                .orElseGet(() -> deviceRepository.save(Device.builder()
                        .deviceId(request.getDeviceId())
                        .registeredAt(OffsetDateTime.now())
                        .build()));
        return DeviceResponse.builder()
                .id(device.getId())
                .deviceId(device.getDeviceId())
                .registeredAt(device.getRegisteredAt())
                .build();
    }
}
