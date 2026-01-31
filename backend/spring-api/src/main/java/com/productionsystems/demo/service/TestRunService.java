package com.productionsystems.demo.service;

import com.productionsystems.demo.domain.Device;
import com.productionsystems.demo.domain.SensorReading;
import com.productionsystems.demo.domain.TestResult;
import com.productionsystems.demo.domain.TestRun;
import com.productionsystems.demo.dto.*;
import com.productionsystems.demo.repository.DeviceRepository;
import com.productionsystems.demo.repository.TestRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestRunService {
    private final DeviceRepository deviceRepository;
    private final TestRunRepository testRunRepository;

    public TestRunResponse submit(TestRunSubmissionRequest request) {
        Device device = deviceRepository.findByDeviceId(request.getDeviceId())
                .orElseGet(() -> deviceRepository.save(Device.builder()
                        .deviceId(request.getDeviceId())
                        .registeredAt(OffsetDateTime.now())
                        .build()));

        TestRun testRun = TestRun.builder()
                .externalRunId(request.getRunId())
                .device(device)
                .startedAt(request.getStartedAt())
                .finishedAt(request.getFinishedAt())
                .totalTests(request.getTotalTests())
                .passed(request.getPassed())
                .failed(request.getFailed())
                .build();

        List<TestResult> results = request.getResults().stream()
                .map(resultRequest -> mapResult(testRun, resultRequest))
                .collect(Collectors.toList());
        testRun.setResults(results);

        TestRun saved = testRunRepository.save(testRun);

        return TestRunResponse.builder()
                .id(saved.getId())
                .runId(saved.getExternalRunId())
                .deviceId(saved.getDevice().getDeviceId())
                .startedAt(saved.getStartedAt())
                .finishedAt(saved.getFinishedAt())
                .totalTests(saved.getTotalTests())
                .passed(saved.getPassed())
                .failed(saved.getFailed())
                .build();
    }

    public List<TestRunResponse> list(String deviceId) {
        List<TestRun> runs = deviceId == null || deviceId.isBlank()
                ? testRunRepository.findAllByOrderByFinishedAtDesc()
                : testRunRepository.findAllByDevice_DeviceIdOrderByFinishedAtDesc(deviceId);

        return runs.stream()
                .map(run -> TestRunResponse.builder()
                        .id(run.getId())
                        .runId(run.getExternalRunId())
                        .deviceId(run.getDevice().getDeviceId())
                        .startedAt(run.getStartedAt())
                        .finishedAt(run.getFinishedAt())
                        .totalTests(run.getTotalTests())
                        .passed(run.getPassed())
                        .failed(run.getFailed())
                        .build())
                .collect(Collectors.toList());
    }

    public MetricsResponse metrics() {
        return MetricsResponse.builder()
                .totalTestRuns(testRunRepository.count())
                .passedRuns(testRunRepository.countPassedRuns())
                .failedRuns(testRunRepository.countFailedRuns())
                .devicesRegistered(deviceRepository.count())
                .build();
    }

    private TestResult mapResult(TestRun testRun, TestResultRequest request) {
        TestResult result = TestResult.builder()
                .testRun(testRun)
                .name(request.getName())
                .status(request.getStatus())
                .message(request.getMessage())
                .startedAt(request.getStartedAt())
                .finishedAt(request.getFinishedAt())
                .durationMs(request.getDurationMs())
                .build();

        List<SensorReading> readings = request.getReadings().stream()
                .map(readingRequest -> SensorReading.builder()
                        .testResult(result)
                        .timestamp(readingRequest.getTimestamp())
                        .humidityPct(readingRequest.getHumidityPct())
                        .temperatureC(readingRequest.getTemperatureC())
                        .rawLine(readingRequest.getRawLine())
                        .build())
                .collect(Collectors.toList());

        result.setReadings(readings);
        return result;
    }
}
