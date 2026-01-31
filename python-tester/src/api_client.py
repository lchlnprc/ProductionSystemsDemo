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
