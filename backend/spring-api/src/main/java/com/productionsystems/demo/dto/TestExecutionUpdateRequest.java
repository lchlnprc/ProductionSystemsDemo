package com.productionsystems.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TestExecutionUpdateRequest {
    @NotBlank
    private String status;

    @Size(max = 500)
    private String message;
}
