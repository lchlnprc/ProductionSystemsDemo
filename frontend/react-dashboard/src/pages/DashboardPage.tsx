import { useMemo } from "react";
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip } from "recharts";
import { useGetLatestTestRunsQuery, useGetTestRunsQuery } from "../store/api";
import { Card } from "../components/Card/Card";
import { StatCard } from "../components/StatCard/StatCard";
import { StatusBadge } from "../components/StatusBadge/StatusBadge";
import { DataTable } from "../components/DataTable/DataTable";
import { ChartCard } from "../components/ChartCard/ChartCard";
import { Skeleton } from "../components/Skeleton/Skeleton";
import styles from "./DashboardPage.module.css";

export function DashboardPage() {
  const { data, isLoading } = useGetLatestTestRunsQuery(undefined, {
    pollingInterval: 5000
  });
  const { data: runs } = useGetTestRunsQuery();

  const recentRows = useMemo(() => {
    if (!runs) return [];
    return runs.slice(0, 6).map((run) => [
      run.runId,
      run.deviceId,
      new Date(run.finishedAt).toLocaleTimeString(),
      <StatusBadge
        key={run.id}
        status={run.failed > 0 ? "failed" : "passed"}
      />
    ]);
  }, [runs]);

  return (
    <div className={styles.page}>
      <div className={styles.statsGrid}>
        <StatCard label="Active test runs" value={data?.activeRuns ?? "--"} />
        <StatCard label="Devices tested today" value={data?.totalDevicesToday ?? "--"} />
        <StatCard label="Pass count" value={data?.passCount ?? "--"} />
        <StatCard label="Fail count" value={data?.failCount ?? "--"} />
        <Card>
          <div className={styles.healthRow}>
            <span>System health</span>
            {data ? <StatusBadge status={data.systemHealth} /> : <Skeleton height={24} />}
          </div>
        </Card>
      </div>

      <div className={styles.grid}>
        <Card title="Recent test runs">
          {isLoading && <Skeleton height={120} />}
          {!isLoading && (
            <DataTable
              headers={["Run ID", "Device", "Finished", "Status"]}
              rows={recentRows}
            />
          )}
        </Card>

        <ChartCard title="Temperature trend">
          {data ? (
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data.temperatureTrend}>
                <XAxis dataKey="time" tickMargin={8} />
                <YAxis tickMargin={8} />
                <Tooltip />
                <Line type="monotone" dataKey="temperature" stroke="#2d6a4f" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <Skeleton height={180} />
          )}
        </ChartCard>

        <ChartCard title="Humidity trend">
          {data ? (
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data.humidityTrend}>
                <XAxis dataKey="time" tickMargin={8} />
                <YAxis tickMargin={8} />
                <Tooltip />
                <Line type="monotone" dataKey="humidity" stroke="#40916c" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <Skeleton height={180} />
          )}
        </ChartCard>
      </div>
    </div>
  );
}
