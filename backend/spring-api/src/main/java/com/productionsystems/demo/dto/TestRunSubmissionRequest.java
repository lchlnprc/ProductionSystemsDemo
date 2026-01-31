package com.productionsystems.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TestRunSubmissionRequest {
    @NotBlank
    private String runId;

    @NotBlank
    private String deviceId;

    @NotNull
    private OffsetDateTime startedAt;

    @NotNull
    private OffsetDateTime finishedAt;

    @NotNull
    private Integer totalTests;

    @NotNull
    private Integer passed;

    @NotNull
    private Integer failed;

    @Valid
    private List<TestResultRequest> results = new ArrayList<>();
}
