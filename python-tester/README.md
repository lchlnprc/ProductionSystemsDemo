# Python Hardware Test Runner

This module communicates with the Arduino over serial, runs automated pytest suites, produces structured JSON results, and posts them to the backend API.

## Tests executed

The runner currently includes the following tests:

- **Connectivity** (`test_connectivity`): reads a sample and asserts temperature and humidity are present.
- **Range validation** (`test_range_validation`): validates temperature and humidity are within configured min/max bounds.
- **Stability** (`test_stability`): samples multiple readings and verifies standard deviation is within thresholds.

## Quick start

1. Connect the Arduino and note the serial port.
2. Install dependencies:
   - `pip install -r requirements.txt`
3. Run the test runner:
   - `python -m src.cli --port /dev/tty.usbserial-XXXX --device-id DEVICE_001`
4. check logs docker compose logs -f python-tester

## Docker usage

Ensure the Arduino is connected and export the serial device path:

- SERIAL_PORT=/dev/cu.usbserial-1230
- DEVICE_ID=DEVICE_001
- API_BASE_URL=http://localhost:8080/api

Build and run using the root Makefile:

- make build
- make run

JSON output is written to ./python-tester/test-results.

## Logging

Logs are JSON-formatted and written to stdout. Set LOG_LEVEL to control verbosity (default: INFO).

## Environment variables

- `SERIAL_PORT` (required if `--port` not set)
- `DEVICE_ID` (default: `UNKNOWN_DEVICE`)
- `API_BASE_URL` (default: `http://localhost:8080/api`)
- `OUTPUT_DIR` (default: `./test-results`)
- `SERIAL_TIMEOUT_S` (default: `2.5`)
- `SKIP_API_POST` (set to `true` to skip posting results)

## Remote test polling

Run the test agent to poll for execution requests:

- `python -m src.cli --poll --device-id DEVICE_001 --api-url http://localhost:8080/api`

## UI-triggered test flow

When an operator clicks **Run Test** in the UI:

1. The frontend posts a **run-test** request to the backend API.
2. The backend records a pending test execution.
3. The Python test runner (agent) polls `/api/test-executions/next`, claims the execution, runs the tests automatically, and patches the execution status/results back to the API.
