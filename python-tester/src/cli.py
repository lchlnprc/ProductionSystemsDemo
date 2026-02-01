from __future__ import annotations

import argparse
import logging
import os
import sys

from rich.console import Console
from rich.panel import Panel

import pytest
from pathlib import Path

from .logging_config import setup_logging


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Production hardware test runner")
    parser.add_argument("--port", help="Serial port path (e.g. /dev/tty.usbserial-XXXX)")
    parser.add_argument("--device-id", help="Device identifier")
    parser.add_argument("--api-url", help="Base URL for backend API (e.g. http://localhost:8080/api)")
    parser.add_argument("--output-dir", help="Directory for JSON test results")
    parser.add_argument("--maxfail", type=int, default=1, help="Stop after N failures")
    parser.add_argument("--poll", action="store_true", help="Poll backend for test executions")
    parser.add_argument("--poll-interval", type=float, default=5.0, help="Polling interval in seconds")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    if args.port:
        os.environ["SERIAL_PORT"] = args.port
    if args.device_id:
        os.environ["DEVICE_ID"] = args.device_id
    if args.api_url:
        os.environ["API_BASE_URL"] = args.api_url
    if args.output_dir:
        os.environ["OUTPUT_DIR"] = args.output_dir

    setup_logging()
    logger = logging.getLogger("test-runner")
    console = Console()
    console.print(Panel.fit("Production Systems Hardware Test Runner", style="bold green"))
    logger.info("Starting test run")

    tests_path = Path(__file__).resolve().parents[1] / "tests"

    def run_tests() -> int:
        pytest_args = [
            "-q",
            "--disable-warnings",
            f"--maxfail={args.maxfail}",
            "-s",
            "-o",
            "log_cli=true",
            "-o",
            f"log_cli_level={os.getenv('LOG_LEVEL','INFO')}",
            str(tests_path),
        ]
        return pytest.main(pytest_args)

    if args.poll:
        from time import sleep
        from .api_client import ApiClient

        api = ApiClient(os.getenv("API_BASE_URL", "http://localhost:8080/api"), 5.0)
        device_id = os.getenv("DEVICE_ID")
        logger.info("Polling for test executions", extra={"deviceId": device_id})

        while True:
            try:
                execution = api.get_next_execution(device_id)
                if not execution:
                    sleep(args.poll_interval)
                    continue
                execution_id = execution.get("id")
                logger.info("Claimed execution", extra={"executionId": execution_id})
                exit_code = run_tests()
                if exit_code == 0:
                    api.update_execution(execution_id, "SUCCESS")
                else:
                    api.update_execution(execution_id, "FAILED", "Tests failed")
            except Exception as exc:  # noqa: BLE001
                logger.error("Polling loop error", exc_info=exc)
                sleep(args.poll_interval)
        return 0

    exit_code = run_tests()

    if exit_code == 0:
        console.print("✅ All tests passed", style="bold green")
        logger.info("Test run completed successfully")
    else:
        console.print("❌ Tests failed", style="bold red")
        logger.error("Test run failed", extra={"exit_code": exit_code})
    return exit_code


if __name__ == "__main__":
    sys.exit(main())
