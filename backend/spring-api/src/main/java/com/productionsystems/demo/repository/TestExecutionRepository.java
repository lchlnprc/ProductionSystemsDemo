package com.productionsystems.demo.repository;

import com.productionsystems.demo.domain.TestExecution;
import com.productionsystems.demo.domain.TestExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface TestExecutionRepository extends JpaRepository<TestExecution, UUID> {
	Optional<TestExecution> findFirstByStatusOrderByRequestedAtAsc(TestExecutionStatus status);

	Optional<TestExecution> findFirstByDeviceIdAndStatusOrderByRequestedAtAsc(String deviceId, TestExecutionStatus status);
}
