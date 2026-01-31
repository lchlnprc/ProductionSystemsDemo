PROJECT_NAME=production-systems

build:
	docker compose build python-tester

run:
	docker compose run --rm python-tester

test:
	docker compose run --rm python-tester python -m pytest -q --disable-warnings
