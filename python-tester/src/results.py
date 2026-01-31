from __future__ import annotations

import json
import uuid
from contextlib import contextmanager
from dataclasses import dataclass, field
from datetime import datetime, timezone
from pathlib import Path
from typing import Iterator

from pydantic import BaseModel, Field


class SensorReading(BaseModel):
    timestamp: str
    humidity_pct: float
    temperature_c: float
    raw_line: str


class TestResult(BaseModel):
    name: str
    status: str
    message: str | None = None
    started_at: str
    finished_at: str | None = None
    duration_ms: int | None = None
    readings: list[SensorReading] = Field(default_factory=list)


class TestRunResult(BaseModel):
    run_id: str
    device_id: str
    started_at: str
    finished_at: str
    total_tests: int
    passed: int
    failed: int
    results: list[TestResult]


@dataclass
class TestRecorder:
    device_id: str
    run_id: str = field(default_factory=lambda: str(uuid.uuid4()))
    started_at: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    tests: list[TestResult] = field(default_factory=list)

    @contextmanager
    def record_test(self, name: str) -> Iterator[TestResult]:
        started = datetime.now(timezone.utc)
        result = TestResult(
            name=name,
            status="running",
            started_at=started.isoformat(),
        )
        self.tests.append(result)
        try:
            yield result
        except Exception as exc:  # noqa: BLE001
            result.status = "failed"
            result.message = str(exc)
            raise
        else:
            result.status = "passed"
        finally:
            finished = datetime.now(timezone.utc)
            result.finished_at = finished.isoformat()
            result.duration_ms = int((finished - started).total_seconds() * 1000)

    def to_run_result(self) -> TestRunResult:
        finished = datetime.now(timezone.utc)
        passed = sum(1 for t in self.tests if t.status == "passed")
        failed = sum(1 for t in self.tests if t.status == "failed")
        return TestRunResult(
            run_id=self.run_id,
            device_id=self.device_id,
            started_at=self.started_at.isoformat(),
            finished_at=finished.isoformat(),
            total_tests=len(self.tests),
            passed=passed,
            failed=failed,
            results=self.tests,
        )

    def write_json(self, output_dir: Path) -> Path:
        output_dir.mkdir(parents=True, exist_ok=True)
        result = self.to_run_result()
        file_path = output_dir / f"test-run-{self.run_id}.json"
        file_path.write_text(json.dumps(result.model_dump(), indent=2))
        return file_path
