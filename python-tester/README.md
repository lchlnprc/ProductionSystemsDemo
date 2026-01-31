# Python Hardware Test Runner

This module communicates with the Arduino over serial, runs automated pytest suites, produces structured JSON results, and posts them to the backend API.

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
