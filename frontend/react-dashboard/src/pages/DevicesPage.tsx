import { useMemo, useState } from "react";
import { useGetDevicesQuery, useGetDeviceTestRunsQuery } from "../store/api";
import { Card } from "../components/Card/Card";
import { StatusBadge } from "../components/StatusBadge/StatusBadge";
import { DataTable } from "../components/DataTable/DataTable";
import { Skeleton } from "../components/Skeleton/Skeleton";
import styles from "./DevicesPage.module.css";

export function DevicesPage() {
  const { data: devices, isLoading } = useGetDevicesQuery();
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [filter, setFilter] = useState<"all" | "passed" | "failed">("all");
  const { data: history } = useGetDeviceTestRunsQuery(selectedId ?? "", {
    skip: !selectedId
  });

  const deviceRows = useMemo(() => {
    if (!devices) return [];
    return devices.map((device) => [
      device.deviceId,
      new Date(device.registeredAt).toLocaleDateString(),
      <StatusBadge key={device.id} status={device.health} />,
      <button className={styles.linkButton} onClick={() => setSelectedId(device.id)}>
        View history
      </button>
    ]);
  }, [devices]);

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
          <DataTable headers={["Device", "Registered", "Health", ""]} rows={deviceRows} />
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
    </div>
  );
}
