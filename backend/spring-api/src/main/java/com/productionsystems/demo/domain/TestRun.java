package com.productionsystems.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "test_runs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_test_runs_external_id", columnNames = "external_run_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestRun {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "external_run_id", nullable = false)
    private String externalRunId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id", nullable = false, foreignKey = @ForeignKey(name = "fk_test_runs_device"))
    private Device device;

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "finished_at", nullable = false)
    private OffsetDateTime finishedAt;

    @Column(name = "total_tests", nullable = false)
    private int totalTests;

    @Column(name = "passed", nullable = false)
    private int passed;

    @Column(name = "failed", nullable = false)
    private int failed;

    @OneToMany(mappedBy = "testRun", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestResult> results = new ArrayList<>();
}
