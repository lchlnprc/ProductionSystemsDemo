import { useEffect, useMemo, useState } from "react";
import {
  useGetDevicesQuery,
  useGetDeviceTestRunsQuery,
  useRunTestMutation,
  useGetTestExecutionQuery
} from "../store/api";
import { Card } from "../components/Card/Card";
import { StatusBadge } from "../components/StatusBadge/StatusBadge";
import { DataTable } from "../components/DataTable/DataTable";
import { Skeleton } from "../components/Skeleton/Skeleton";
import { Toast } from "../components/Toast/Toast";
import styles from "./DevicesPage.module.css";

export function DevicesPage() {
  const { data: devices, isLoading } = useGetDevicesQuery();
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [filter, setFilter] = useState<"all" | "passed" | "failed">("all");
  const [activeExecutionId, setActiveExecutionId] = useState<string | null>(null);
  const [activeDeviceId, setActiveDeviceId] = useState<string | null>(null);
  const [toast, setToast] = useState<{ message: string; tone: "success" | "error" | "info" } | null>(null);
  const { data: history } = useGetDeviceTestRunsQuery(selectedId ?? "", {
    skip: !selectedId
  });
  const [runTest, { isLoading: isTriggering }] = useRunTestMutation();
  const executionQuery = useGetTestExecutionQuery(activeExecutionId ?? "", {
    skip: !activeExecutionId,
    pollingInterval: 2000
  });

  const executionStatus = executionQuery.data?.status ?? "IDLE";

  const handleRunTest = async (deviceId: string) => {
    setActiveDeviceId(deviceId);
    setToast({ message: "Test requested", tone: "info" });
    try {
      const response = await runTest({ deviceId, requestedBy: "operator" }).unwrap();
      setActiveExecutionId(response.id);
    } catch {
      setToast({ message: "Failed to trigger test", tone: "error" });
    }
  };

  useEffect(() => {
    if (!executionQuery.data) return;
    if (executionQuery.data.status === "SUCCESS") {
      setToast({ message: "Test completed", tone: "success" });
    }
    if (executionQuery.data.status === "FAILED") {
      setToast({ message: "Test failed", tone: "error" });
    }
  }, [executionQuery.data?.status]);

  const deviceRows = useMemo(() => {
    if (!devices) return [];
    return devices.map((device) => {
      const isActive = device.id === activeDeviceId;
      const status = isActive ? executionStatus : "IDLE";
      return [
      device.deviceId,
      new Date(device.registeredAt).toLocaleDateString(),
      <StatusBadge key={device.id} status={device.health} />,
      <div className={styles.actionGroup}>
        <button className={styles.linkButton} onClick={() => setSelectedId(device.id)}>
          View history
        </button>
        <button
          className={`${styles.runButton} ${styles[status.toLowerCase()] ?? ""}`}
          onClick={() => handleRunTest(device.deviceId)}
          disabled={isTriggering && isActive}
        >
          {status === "RUNNING" && <span className={styles.spinner} />}
          {status === "SUCCESS" ? "Success" : status === "FAILED" ? "Failed" : "Run Test"}
        </button>
      </div>
    ];
    });
  }, [devices, activeDeviceId, executionStatus, isTriggering]);

  const filteredHistory = useMemo(() => {
    if (!history) return [];
    if (filter === "all") return history;
    return history.filter((run) => run.status === filter);
  }, [history, filter]);

  return (
    <div className={styles.page}>
      <Card title="Devices">
        {isLoading && <Skeleton height={120} />}
        {!isLoading && (
          <DataTable headers={["Device", "Registered", "Health", "Actions"]} rows={deviceRows} />
        )}
      </Card>

      <Card title="Device test history">
        <div className={styles.filterRow}>
          <span>Filter:</span>
          <button className={filter === "all" ? styles.active : ""} onClick={() => setFilter("all")}>
            All
          </button>
          <button className={filter === "passed" ? styles.active : ""} onClick={() => setFilter("passed")}>
            Passed
          </button>
          <button className={filter === "failed" ? styles.active : ""} onClick={() => setFilter("failed")}>
            Failed
          </button>
        </div>

        {!selectedId && <p className={styles.muted}>Select a device to view history.</p>}
        {selectedId && (
          <DataTable
            headers={["Run", "Status", "Finished"]}
            rows={filteredHistory.map((run) => [
              run.runId,
              <StatusBadge key={run.id} status={run.status} />,
              new Date(run.finishedAt).toLocaleString()
            ])}
          />
        )}
      </Card>

      {activeExecutionId && (
        <Card title="Live test progress">
          <div className={styles.progressPanel}>
            <div className={styles.progressRow}>
              <span>Status</span>
              <StatusBadge
                status={
                  executionStatus === "SUCCESS"
                    ? "passed"
                    : executionStatus === "FAILED"
                      ? "failed"
                      : "running"
                }
              />
            </div>
            <div className={styles.logViewer}>
              <p>Execution ID: {activeExecutionId}</p>
              <p>Device ID: {activeDeviceId}</p>
              <p>State: {executionStatus}</p>
              {executionQuery.data?.message && <p>Message: {executionQuery.data.message}</p>}
            </div>
          </div>
        </Card>
      )}

      {toast && (
        <div className={styles.toastHost}>
          <Toast message={toast.message} tone={toast.tone} onClose={() => setToast(null)} />
        </div>
      )}
    </div>
  );
}
