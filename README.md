# ProductionSystemsDemo

## Python hardware test runner (containerized)

1. Connect the Arduino and identify the serial port.
2. Export required environment variables:
	- SERIAL_PORT=/dev/tty.usbserial-XXXX
	- DEVICE_ID=DEVICE_001
	- API_BASE_URL=http://localhost:8080/api
3. Build and run:
	- make build
	- make run

Logs are JSON-formatted to stdout. Results are written to ./python-tester/test-results.
# ProductionSystemsDemo