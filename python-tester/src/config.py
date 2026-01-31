from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class Config:
    serial_port: str
    baudrate: int
    read_timeout_s: float
    api_base_url: str
    api_timeout_s: float
    output_dir: Path
    device_id: str
    min_temperature_c: float
    max_temperature_c: float
    min_humidity_pct: float
    max_humidity_pct: float
    stability_samples: int
    max_temperature_stddev: float
    max_humidity_stddev: float


def load_config() -> Config:
    serial_port = os.getenv("SERIAL_PORT", "")
    return Config(
        serial_port=serial_port,
        baudrate=int(os.getenv("SERIAL_BAUDRATE", "115200")),
        read_timeout_s=float(os.getenv("SERIAL_TIMEOUT_S", "2.5")),
        api_base_url=os.getenv("API_BASE_URL", "http://localhost:8080/api"),
        api_timeout_s=float(os.getenv("API_TIMEOUT_S", "5.0")),
        output_dir=Path(os.getenv("OUTPUT_DIR", "./test-results")),
        device_id=os.getenv("DEVICE_ID", "UNKNOWN_DEVICE"),
        min_temperature_c=float(os.getenv("MIN_TEMPERATURE_C", "0")),
        max_temperature_c=float(os.getenv("MAX_TEMPERATURE_C", "50")),
        min_humidity_pct=float(os.getenv("MIN_HUMIDITY_PCT", "20")),
        max_humidity_pct=float(os.getenv("MAX_HUMIDITY_PCT", "90")),
        stability_samples=int(os.getenv("STABILITY_SAMPLES", "10")),
        max_temperature_stddev=float(os.getenv("MAX_TEMPERATURE_STDDEV", "1.5")),
        max_humidity_stddev=float(os.getenv("MAX_HUMIDITY_STDDEV", "3.0")),
    )
