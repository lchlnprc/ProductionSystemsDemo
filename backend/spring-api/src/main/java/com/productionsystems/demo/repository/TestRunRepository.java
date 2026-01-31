package com.productionsystems.demo.repository;

import com.productionsystems.demo.domain.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TestRunRepository extends JpaRepository<TestRun, UUID> {
    List<TestRun> findAllByDevice_DeviceIdOrderByFinishedAtDesc(String deviceId);

    List<TestRun> findAllByOrderByFinishedAtDesc();

    @Query("select count(tr) from TestRun tr where tr.failed = 0")
    long countPassedRuns();

    @Query("select count(tr) from TestRun tr where tr.failed > 0")
    long countFailedRuns();
}
