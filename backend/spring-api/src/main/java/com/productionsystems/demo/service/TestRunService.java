package com.productionsystems.demo.service;

import com.productionsystems.demo.domain.Device;
import com.productionsystems.demo.domain.SensorReading;
import com.productionsystems.demo.domain.TestResult;
import com.productionsystems.demo.domain.TestRun;
import com.productionsystems.demo.dto.*;
import com.productionsystems.demo.repository.DeviceRepository;
import com.productionsystems.demo.repository.SensorReadingRepository;
import com.productionsystems.demo.repository.TestRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestRunService {
    private final DeviceRepository deviceRepository;
    private final TestRunRepository testRunRepository;
        private final SensorReadingRepository sensorReadingRepository;

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
                .build();

        List<TestResult> results = request.getResults().stream()
                .map(resultRequest -> mapResult(testRun, resultRequest))
                .collect(Collectors.toList());
        testRun.setResults(results);

        int totalTests = results.size();
        int passed = (int) results.stream().filter(result -> "passed".equalsIgnoreCase(result.getStatus())).count();
        int failed = (int) results.stream().filter(result -> "failed".equalsIgnoreCase(result.getStatus())).count();

        testRun.setTotalTests(totalTests);
        testRun.setPassed(passed);
        testRun.setFailed(failed);

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

        @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public LatestDashboardResponse latest() {
        MetricsResponse metrics = metrics();
        List<TestRunResponse> recentRuns = testRunRepository.findTop6ByOrderByFinishedAtDesc().stream()
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

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        long devicesToday = testRunRepository.findAllByOrderByFinishedAtDesc().stream()
                .filter(run -> run.getFinishedAt().toLocalDate().equals(today))
                .map(run -> run.getDevice().getDeviceId())
                .distinct()
                .count();

        OffsetDateTime recentThreshold = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(5);
        long activeRuns = testRunRepository.findAllByOrderByFinishedAtDesc().stream()
                .filter(run -> run.getFinishedAt().isAfter(recentThreshold))
                .count();

        String systemHealth = metrics.getFailedRuns() == 0 ? "ok"
                : (metrics.getFailedRuns() > metrics.getPassedRuns() ? "critical" : "warning");

        List<SensorReading> latestReadings = sensorReadingRepository.findTop50ByOrderByTimestampDesc();
        List<TemperatureTrendPoint> temperatureTrend = latestReadings.stream()
                .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .map(reading -> TemperatureTrendPoint.builder()
                        .time(reading.getTimestamp().toString())
                        .temperature(reading.getTemperatureC())
                        .build())
                .collect(Collectors.toList());

        List<HumidityTrendPoint> humidityTrend = latestReadings.stream()
                .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .map(reading -> HumidityTrendPoint.builder()
                        .time(reading.getTimestamp().toString())
                        .humidity(reading.getHumidityPct())
                        .build())
                .collect(Collectors.toList());

        return LatestDashboardResponse.builder()
                .activeRuns(activeRuns)
                .totalDevicesToday(devicesToday)
                .passCount(metrics.getPassedRuns())
                .failCount(metrics.getFailedRuns())
                .systemHealth(systemHealth)
                .recentRuns(recentRuns)
                .temperatureTrend(temperatureTrend)
                .humidityTrend(humidityTrend)
                .build();
    }

    @Transactional(readOnly = true)
    public TestRunDetailResponse getById(UUID id) {
        TestRun run = testRunRepository.findWithResultsById(id)
                .orElseThrow();

        return TestRunDetailResponse.builder()
                .id(run.getId())
                .runId(run.getExternalRunId())
                .deviceId(run.getDevice().getDeviceId())
                .startedAt(run.getStartedAt())
                .finishedAt(run.getFinishedAt())
                .totalTests(run.getTotalTests())
                .passed(run.getPassed())
                .failed(run.getFailed())
                .results(run.getResults().stream()
                        .map(result -> TestResultDetailResponse.builder()
                                .name(result.getName())
                                .status(result.getStatus())
                                .message(result.getMessage())
                                .durationMs(result.getDurationMs())
                                .readings(result.getReadings().stream()
                                        .map(reading -> SensorReadingResponse.builder()
                                                .timestamp(reading.getTimestamp())
                                                .humidityPct(reading.getHumidityPct())
                                                .temperatureC(reading.getTemperatureC())
                                                .rawLine(reading.getRawLine())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
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
