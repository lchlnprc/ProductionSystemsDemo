package com.productionsystems.demo.queue;

import java.util.UUID;

public record TestExecutionCommand(UUID executionId, String deviceId) {
}
