from __future__ import annotations

import requests

from .results import TestRunResult


class ApiClient:
    def __init__(self, base_url: str, timeout_s: float) -> None:
        self.base_url = base_url.rstrip("/")
        self.timeout_s = timeout_s

    def post_test_run(self, result: TestRunResult) -> None:
        url = f"{self.base_url}/test-runs"
        response = requests.post(url, json=result.model_dump(), timeout=self.timeout_s)
        response.raise_for_status()

    def get_next_execution(self, device_id: str | None) -> dict | None:
        params = {"deviceId": device_id} if device_id else None
        url = f"{self.base_url}/test-executions/next"
        response = requests.get(url, params=params, timeout=self.timeout_s)
        if response.status_code == 204:
            return None
        response.raise_for_status()
        return response.json()

    def update_execution(self, execution_id: str, status: str, message: str | None = None) -> None:
        url = f"{self.base_url}/test-executions/{execution_id}"
        payload = {"status": status}
        if message:
            payload["message"] = message
        response = requests.patch(url, json=payload, timeout=self.timeout_s)
        response.raise_for_status()
