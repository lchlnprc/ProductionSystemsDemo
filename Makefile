PROJECT_NAME=production-systems
SERIAL_PORT ?= /dev/cu.usbserial-1230
SOCAT_PORT ?= 4000
SOCAT_PID_FILE ?= .socat.pid

build:
	docker compose build python-tester

run:
	docker compose run --rm python-tester

test:
	docker compose run --rm python-tester python -m pytest -q --disable-warnings

socat-start:
	@if lsof -nP -iTCP:$(SOCAT_PORT) -sTCP:LISTEN >/dev/null 2>&1; then \
		echo "socat already listening on port $(SOCAT_PORT)"; \
	else \
		echo "Starting socat bridge on port $(SOCAT_PORT) -> $(SERIAL_PORT)"; \
		nohup socat -d -d TCP-LISTEN:$(SOCAT_PORT),reuseaddr,fork OPEN:$(SERIAL_PORT),raw,echo=0,ispeed=115200,ospeed=115200 >/tmp/socat.$(SOCAT_PORT).log 2>&1 & \
		echo $$! > $(SOCAT_PID_FILE); \
	fi

socat-stop:
	@if [ -f $(SOCAT_PID_FILE) ]; then \
		kill $$(cat $(SOCAT_PID_FILE)) >/dev/null 2>&1 || true; \
		rm -f $(SOCAT_PID_FILE); \
		echo "Stopped socat"; \
	else \
		pkill socat >/dev/null 2>&1 || true; \
		echo "Stopped any running socat"; \
	fi

run-with-bridge: socat-start
	@docker compose up -d --build backend postgres
	@SERIAL_PORT=socket://host.docker.internal:$(SOCAT_PORT) \
		API_BASE_URL=http://backend:8080/api \
		docker compose run --rm -e SERIAL_PORT -e API_BASE_URL python-tester
	@$(MAKE) socat-stop
