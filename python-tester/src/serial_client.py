from __future__ import annotations

import time
from urllib.parse import urlencode, urlsplit, urlunsplit, parse_qs
from dataclasses import dataclass

import serial
from serial.serialutil import SerialException


@dataclass
class SerialClient:
    port: str
    baudrate: int
    timeout_s: float
    _serial: serial.Serial | None = None

    def _sanitize_socket_url(self, port: str) -> str:
        if not port.startswith("socket://"):
            return port
        parts = urlsplit(port)
        if not parts.query:
            return port
        params = parse_qs(parts.query, keep_blank_values=True)
        params.pop("reconnect", None)
        new_query = urlencode(params, doseq=True)
        return urlunsplit((parts.scheme, parts.netloc, parts.path, new_query, parts.fragment))

    def open(self) -> None:
        if self._serial and self._serial.is_open:
            return
        port = self._sanitize_socket_url(self.port)
        if port.startswith("socket://"):
            self._serial = serial.serial_for_url(
                port, baudrate=self.baudrate, timeout=self.timeout_s
            )
        else:
            self._serial = serial.Serial(
                port, self.baudrate, timeout=self.timeout_s
            )
        time.sleep(1.0)
        self._serial.reset_input_buffer()

    def close(self) -> None:
        if self._serial and self._serial.is_open:
            self._serial.close()

    def read_line(self) -> str | None:
        if not self._serial or not self._serial.is_open:
            raise RuntimeError("Serial port is not open")
        try:
            raw = self._serial.readline()
        except (SerialException, OSError):
            self.close()
            time.sleep(0.5)
            self.open()
            return None
        if not raw:
            return None
        return raw.decode("utf-8", errors="replace").strip()
