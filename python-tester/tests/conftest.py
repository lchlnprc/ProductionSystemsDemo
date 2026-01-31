from __future__ import annotations

import logging
import os
from pathlib import Path
import sys

import pytest
from rich.console import Console

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))

from src.api_client import ApiClient
from src.config import load_config
from src.logging_config import setup_logging
from src.results import TestRecorder
from src.sensor_reader import SensorReader
from src.serial_client import SerialClient


@pytest.fixture(scope="session")
def config():
    cfg = load_config()
    if not cfg.serial_port:
        pytest.skip("SERIAL_PORT not set and no --port provided")
    return cfg


setup_logging()
logger = logging.getLogger("test-runner")


@pytest.fixture(scope="session")
def console() -> Console:
    return Console()


@pytest.fixture(scope="session")
def serial_client(config):
    client = SerialClient(config.serial_port, config.baudrate, config.read_timeout_s)
    client.open()
    yield client
    client.close()


@pytest.fixture(scope="session")
def sensor_reader(config, serial_client):
    return SensorReader(serial_client, config.read_timeout_s)


@pytest.fixture(scope="session")
def recorder(config) -> TestRecorder:
    return TestRecorder(device_id=config.device_id)


def pytest_sessionfinish(session, exitstatus):
    cfg = load_config()
    recorder: TestRecorder | None = session.config._test_recorder if hasattr(session.config, "_test_recorder") else None
    if recorder is None:
        return

    output_path = recorder.write_json(cfg.output_dir)
    console = Console()
    console.print(f"JSON results: {output_path}", style="bold blue")
    logger.info("Wrote JSON results", extra={"path": str(output_path)})

    if os.getenv("SKIP_API_POST", "").lower() in {"1", "true", "yes"}:
        console.print("API post skipped", style="bold yellow")
        logger.info("API post skipped")
        return

    api_client = ApiClient(cfg.api_base_url, cfg.api_timeout_s)
    try:
        api_client.post_test_run(recorder.to_run_result())
        console.print("Results posted to API", style="bold green")
        logger.info("Posted results to API")
    except Exception as exc:  # noqa: BLE001
        console.print(f"API post failed: {exc}", style="bold yellow")
        logger.error("API post failed", exc_info=exc)


def pytest_configure(config):
    cfg = load_config()
    if not cfg.serial_port:
        return
    config._test_recorder = TestRecorder(device_id=cfg.device_id)


@pytest.fixture(scope="session", autouse=True)
def bind_recorder(request, recorder):
    request.config._test_recorder = recorder
    return recorder
