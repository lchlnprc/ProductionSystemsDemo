package com.productionsystems.demo.repository;

import com.productionsystems.demo.domain.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SensorReadingRepository extends JpaRepository<SensorReading, UUID> {
    List<SensorReading> findTop50ByOrderByTimestampDesc();
}
