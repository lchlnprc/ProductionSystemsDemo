package com.productionsystems.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "sensor_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorReading {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "test_result_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sensor_readings_test_result"))
    private TestResult testResult;

    @Column(name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "humidity_pct", nullable = false)
    private double humidityPct;

    @Column(name = "temperature_c", nullable = false)
    private double temperatureC;

    @Column(name = "raw_line", columnDefinition = "text")
    private String rawLine;
}
