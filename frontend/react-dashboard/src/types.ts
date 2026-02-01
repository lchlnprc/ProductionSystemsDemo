export type Status = "passed" | "failed" | "running";

export interface TestRunSummary {
  id: string;
  runId: string;
  deviceId: string;
  startedAt: string;
  finishedAt: string;
  totalTests: number;
  passed: number;
  failed: number;
}

export interface TestRunLatestResponse {
  activeRuns: number;
  totalDevicesToday: number;
  passCount: number;
  failCount: number;
  systemHealth: "ok" | "warning" | "critical";
  recentRuns: TestRunSummary[];
  temperatureTrend: Array<{ time: string; temperature: number }>;
  humidityTrend: Array<{ time: string; humidity: number }>;
}

export interface Device {
  id: string;
  deviceId: string;
  registeredAt: string;
  health: "ok" | "warning" | "critical";
}

export interface DeviceTestRun {
  id: string;
  runId: string;
  status: Status;
  finishedAt: string;
}

export interface TestRunDetail {
  id: string;
  runId: string;
  deviceId: string;
  startedAt: string;
  finishedAt: string;
  totalTests: number;
  passed: number;
  failed: number;
  results: Array<{
    name: string;
    status: Status;
    message?: string | null;
    durationMs?: number | null;
    readings: Array<{
      timestamp: string;
      humidityPct: number;
      temperatureC: number;
      rawLine?: string | null;
    }>;
  }>;
}

export interface TestExecutionResponse {
  id: string;
  deviceId: string;
  status: "PENDING" | "RUNNING" | "SUCCESS" | "FAILED";
  requestedAt: string;
  startedAt?: string | null;
  finishedAt?: string | null;
  message?: string | null;
}
