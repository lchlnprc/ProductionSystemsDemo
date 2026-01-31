package com.productionsystems.demo.service;

import com.productionsystems.demo.domain.Device;
import com.productionsystems.demo.dto.DeviceRegistrationRequest;
import com.productionsystems.demo.dto.DeviceResponse;
import com.productionsystems.demo.dto.DeviceTestRunResponse;
import com.productionsystems.demo.repository.DeviceRepository;
import com.productionsystems.demo.repository.TestRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final TestRunRepository testRunRepository;

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
            .health(resolveHealth(device.getId()))
                .build();
    }

        @Transactional(readOnly = true)
        public List<DeviceResponse> list() {
        return deviceRepository.findAll().stream()
            .sorted(Comparator.comparing(Device::getRegisteredAt).reversed())
            .map(device -> DeviceResponse.builder()
                .id(device.getId())
                .deviceId(device.getDeviceId())
                .registeredAt(device.getRegisteredAt())
                .health(resolveHealth(device.getId()))
                .build())
            .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<DeviceTestRunResponse> testRuns(UUID deviceId) {
        return testRunRepository.findAllByDevice_IdOrderByFinishedAtDesc(deviceId).stream()
            .map(run -> DeviceTestRunResponse.builder()
                .id(run.getId())
                .runId(run.getExternalRunId())
                .status(run.getFailed() > 0 ? "failed" : "passed")
                .finishedAt(run.getFinishedAt())
                .build())
            .collect(Collectors.toList());
        }

        private String resolveHealth(UUID deviceId) {
        return testRunRepository.findAllByDevice_IdOrderByFinishedAtDesc(deviceId).stream()
            .findFirst()
            .map(run -> run.getFailed() > 0 ? "warning" : "ok")
            .orElse("ok");
        }
}
