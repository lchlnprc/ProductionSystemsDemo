# ProductionSystemsDemo

## Python hardware test runner (containerized)

1. Connect the Arduino and identify the serial port.
2. Expose serial device to TCP Server
    - socat -d -d TCP-LISTEN:4000,reuseaddr,fork FILE:/dev/cu.usbserial-1230,raw,echo=0
3. Export required environment variables:
	- SERIAL_PORT=/dev/cu.usbserial-1230
	- DEVICE_ID=DEVICE_001
	- API_BASE_URL=http://localhost:8080/api
4. Build and run:
	- make build
	- make run

### Serial bridge (macOS)

If you are using Docker Desktop on macOS, use the TCP serial bridge:

- make run-with-bridge

You can customize the device and port:

- make run-with-bridge SERIAL_PORT=/dev/cu.usbserial-1230 SOCAT_PORT=4000

The bridge uses a macOS-compatible socat command with `OPEN:` and `ispeed/ospeed`.

Logs are JSON-formatted to stdout. Results are written to ./python-tester/test-results.
# ProductionSystemsDemo