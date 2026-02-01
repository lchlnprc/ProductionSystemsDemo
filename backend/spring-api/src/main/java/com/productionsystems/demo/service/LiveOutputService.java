package com.productionsystems.demo.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class LiveOutputService {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final String tcpHost;
    private final int tcpPort;

    public LiveOutputService(
            @Value("${live-output.host:host.docker.internal}") String tcpHost,
            @Value("${live-output.port:4000}") int tcpPort
    ) {
        this.tcpHost = tcpHost;
        this.tcpPort = tcpPort;
    }

    public SseEmitter stream(String deviceId) {
        SseEmitter emitter = new SseEmitter(0L);
        executor.submit(() -> {
            try (Socket socket = new Socket(tcpHost, tcpPort);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    emitter.send(SseEmitter.event()
                            .name("log")
                            .data(line));
                }
                emitter.complete();
            } catch (Exception ex) {
                log.error("Live output stream failed", ex);
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }
}
