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
    pytest_args = ["-q", "--disable-warnings", f"--maxfail={args.maxfail}", str(tests_path)]
    exit_code = pytest.main(pytest_args)

    if exit_code == 0:
        console.print("✅ All tests passed", style="bold green")
        logger.info("Test run completed successfully")
    else:
        console.print("❌ Tests failed", style="bold red")
        logger.error("Test run failed", extra={"exit_code": exit_code})
    return exit_code


if __name__ == "__main__":
    sys.exit(main())
