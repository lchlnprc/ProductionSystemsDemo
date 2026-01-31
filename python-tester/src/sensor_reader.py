from __future__ import annotations

import re
import time
from dataclasses import dataclass
from datetime import datetime, timezone

from .results import SensorReading
from .serial_client import SerialClient


LINE_PATTERN = re.compile(
    r"DHT11\tOK\tHumidity:\s*([0-9.]+)\s*\t\tTemperature:\s*([0-9.]+)",
    re.IGNORECASE,
)


@dataclass
class SensorReader:
    client: SerialClient
    timeout_s: float

    def read_sample(self) -> SensorReading:
        deadline = time.time() + self.timeout_s
        last_line = ""
        while time.time() < deadline:
            line = self.client.read_line()
            if not line:
                continue
            last_line = line
            match = LINE_PATTERN.search(line)
            if match:
                humidity = float(match.group(1))
                temperature = float(match.group(2))
                return SensorReading(
                    timestamp=datetime.now(timezone.utc).isoformat(),
                    humidity_pct=humidity,
                    temperature_c=temperature,
                    raw_line=line,
                )
            if "ERROR" in line.upper():
                last_line = line
        raise TimeoutError(f"No valid sensor reading within {self.timeout_s}s. Last line: {last_line}")
