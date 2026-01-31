from __future__ import annotations

import time
from dataclasses import dataclass

import serial


@dataclass
class SerialClient:
    port: str
    baudrate: int
    timeout_s: float
    _serial: serial.Serial | None = None

    def open(self) -> None:
        if self._serial and self._serial.is_open:
            return
        if self.port.startswith("socket://"):
            self._serial = serial.serial_for_url(
                self.port, baudrate=self.baudrate, timeout=self.timeout_s
            )
        else:
            self._serial = serial.Serial(
                self.port, self.baudrate, timeout=self.timeout_s
            )
        time.sleep(1.0)
        self._serial.reset_input_buffer()

    def close(self) -> None:
        if self._serial and self._serial.is_open:
            self._serial.close()

    def read_line(self) -> str | None:
        if not self._serial or not self._serial.is_open:
            raise RuntimeError("Serial port is not open")
        raw = self._serial.readline()
        if not raw:
            return None
        return raw.decode("utf-8", errors="replace").strip()
