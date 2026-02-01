package com.productionsystems.demo.queue;

import java.util.function.Consumer;

public interface CommandQueue {
    void enqueue(TestExecutionCommand command);
    void registerConsumer(Consumer<TestExecutionCommand> consumer);
}
