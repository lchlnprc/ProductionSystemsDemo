package com.productionsystems.demo.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TestExecutionRequest {
    @Size(max = 255)
    private String requestedBy;

    @Size(max = 500)
    private String notes;
}
