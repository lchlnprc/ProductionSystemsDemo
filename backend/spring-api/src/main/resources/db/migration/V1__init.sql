CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id VARCHAR(255) NOT NULL UNIQUE,
    registered_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE test_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_run_id VARCHAR(255) NOT NULL UNIQUE,
    device_id UUID NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    finished_at TIMESTAMP WITH TIME ZONE NOT NULL,
    total_tests INTEGER NOT NULL,
    passed INTEGER NOT NULL,
    failed INTEGER NOT NULL,
    CONSTRAINT fk_test_runs_device FOREIGN KEY (device_id) REFERENCES devices (id)
);

CREATE TABLE test_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_run_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    message TEXT,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    finished_at TIMESTAMP WITH TIME ZONE,
    duration_ms INTEGER,
    CONSTRAINT fk_test_results_test_run FOREIGN KEY (test_run_id) REFERENCES test_runs (id)
);

CREATE TABLE sensor_readings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_result_id UUID NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    humidity_pct DOUBLE PRECISION NOT NULL,
    temperature_c DOUBLE PRECISION NOT NULL,
    raw_line TEXT,
    CONSTRAINT fk_sensor_readings_test_result FOREIGN KEY (test_result_id) REFERENCES test_results (id)
);

CREATE INDEX idx_test_runs_device_id ON test_runs(device_id);
CREATE INDEX idx_test_results_test_run_id ON test_results(test_run_id);
CREATE INDEX idx_sensor_readings_test_result_id ON sensor_readings(test_result_id);
