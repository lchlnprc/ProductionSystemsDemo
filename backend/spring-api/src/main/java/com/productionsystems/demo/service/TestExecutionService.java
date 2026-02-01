package com.productionsystems.demo.service;

import com.productionsystems.demo.domain.TestExecution;
import com.productionsystems.demo.domain.TestExecutionStatus;
import com.productionsystems.demo.dto.TestExecutionRequest;
import com.productionsystems.demo.dto.TestExecutionResponse;
import com.productionsystems.demo.dto.TestExecutionUpdateRequest;
import com.productionsystems.demo.queue.CommandQueue;
import com.productionsystems.demo.queue.TestExecutionCommand;
import com.productionsystems.demo.repository.TestExecutionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestExecutionService {
    private final TestExecutionRepository repository;
    private final CommandQueue commandQueue;

    @PostConstruct
    public void init() {
        commandQueue.registerConsumer(this::processCommand);
    }

    @Transactional
    public TestExecutionResponse trigger(String deviceId, TestExecutionRequest request) {
        TestExecution execution = TestExecution.builder()
                .deviceId(deviceId)
                .requestedAt(OffsetDateTime.now())
                .status(TestExecutionStatus.PENDING)
                .message(request != null ? request.getNotes() : null)
                .build();

        TestExecution saved = repository.save(execution);
        log.info("Test execution requested", Map.of("executionId", saved.getId(), "deviceId", deviceId));

        commandQueue.enqueue(new TestExecutionCommand(saved.getId(), deviceId));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TestExecutionResponse get(UUID id) {
        TestExecution execution = repository.findById(id).orElseThrow();
        return toResponse(execution);
    }

    @Transactional
    public Optional<TestExecutionResponse> claimNext(String deviceId) {
        Optional<TestExecution> next = (deviceId == null || deviceId.isBlank())
                ? repository.findFirstByStatusOrderByRequestedAtAsc(TestExecutionStatus.PENDING)
                : repository.findFirstByDeviceIdAndStatusOrderByRequestedAtAsc(deviceId, TestExecutionStatus.PENDING);

        if (next.isEmpty()) {
            return Optional.empty();
        }

        TestExecution execution = next.get();
        execution.setStatus(TestExecutionStatus.RUNNING);
        execution.setStartedAt(OffsetDateTime.now());
        repository.save(execution);
        log.info("Test execution claimed", Map.of("executionId", execution.getId(), "deviceId", execution.getDeviceId()));
        return Optional.of(toResponse(execution));
    }

    @Transactional
    public TestExecutionResponse update(UUID id, TestExecutionUpdateRequest request) {
        TestExecution execution = repository.findById(id).orElseThrow();
        TestExecutionStatus status = TestExecutionStatus.valueOf(request.getStatus().toUpperCase());
        execution.setStatus(status);
        execution.setMessage(request.getMessage());
        if (status == TestExecutionStatus.RUNNING && execution.getStartedAt() == null) {
            execution.setStartedAt(OffsetDateTime.now());
        }
        if ((status == TestExecutionStatus.SUCCESS || status == TestExecutionStatus.FAILED)
                && execution.getFinishedAt() == null) {
            execution.setFinishedAt(OffsetDateTime.now());
        }
        repository.save(execution);
        log.info("Test execution updated", Map.of("executionId", execution.getId(), "status", status.name()));
        return toResponse(execution);
    }

    @Transactional
    public void processCommand(TestExecutionCommand command) {
        repository.findById(command.executionId()).ifPresent(execution -> {
            execution.setStatus(TestExecutionStatus.RUNNING);
            execution.setStartedAt(OffsetDateTime.now());
            repository.save(execution);
            log.info("Test execution running", Map.of("executionId", execution.getId()));

            execution.setStatus(TestExecutionStatus.SUCCESS);
            execution.setFinishedAt(OffsetDateTime.now());
            repository.save(execution);
            log.info("Test execution completed", Map.of("executionId", execution.getId()));
        });
    }

    private TestExecutionResponse toResponse(TestExecution execution) {
        return TestExecutionResponse.builder()
                .id(execution.getId())
                .deviceId(execution.getDeviceId())
                .status(execution.getStatus().name())
                .requestedAt(execution.getRequestedAt())
                .startedAt(execution.getStartedAt())
                .finishedAt(execution.getFinishedAt())
                .message(execution.getMessage())
                .build();
    }
}
