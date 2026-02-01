package com.productionsystems.demo.queue;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@Component
public class InMemoryCommandQueue implements CommandQueue {
    private final BlockingQueue<TestExecutionCommand> queue = new LinkedBlockingQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private Consumer<TestExecutionCommand> consumer;
    private Thread worker;

    @Override
    public void enqueue(TestExecutionCommand command) {
        queue.offer(command);
        log.info("Queued test execution command", java.util.Map.of("executionId", command.executionId()));
    }

    @Override
    public void registerConsumer(Consumer<TestExecutionCommand> consumer) {
        this.consumer = consumer;
    }

    @PostConstruct
    public void start() {
        worker = new Thread(() -> {
            while (running.get()) {
                try {
                    TestExecutionCommand command = queue.take();
                    if (consumer != null) {
                        consumer.accept(command);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ex) {
                    log.error("Command queue consumer failed", ex);
                }
            }
        }, "test-execution-queue");
        worker.start();
    }

    @PreDestroy
    public void stop() {
        running.set(false);
        if (worker != null) {
            worker.interrupt();
        }
    }
}
