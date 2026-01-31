import { useParams } from "react-router-dom";
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip } from "recharts";
import { useGetTestRunByIdQuery } from "../store/api";
import { Card } from "../components/Card/Card";
import { StatusBadge } from "../components/StatusBadge/StatusBadge";
import { DataTable } from "../components/DataTable/DataTable";
import { Skeleton } from "../components/Skeleton/Skeleton";
import styles from "./TestRunDetailPage.module.css";

export function TestRunDetailPage() {
  const { id } = useParams();
  const { data, isLoading } = useGetTestRunByIdQuery(id ?? "");

  if (isLoading) {
    return <Skeleton height={160} />;
  }

  if (!data) {
    return <p className={styles.muted}>No data found.</p>;
  }

  const chartData = data.results.flatMap((result) =>
    result.readings.map((reading) => ({
      time: new Date(reading.timestamp).toLocaleTimeString(),
      temperature: reading.temperatureC,
      humidity: reading.humidityPct
    }))
  );

  return (
    <div className={styles.page}>
      <Card title="Test run metadata">
        <div className={styles.metaGrid}>
          <div>
            <strong>Run ID</strong>
            <p>{data.runId}</p>
          </div>
          <div>
            <strong>Device</strong>
            <p>{data.deviceId}</p>
          </div>
          <div>
            <strong>Status</strong>
            <StatusBadge status={data.failed > 0 ? "failed" : "passed"} />
          </div>
          <div>
            <strong>Duration</strong>
            <p>
              {new Date(data.startedAt).toLocaleTimeString()} -
              {" "}
              {new Date(data.finishedAt).toLocaleTimeString()}
            </p>
          </div>
        </div>
      </Card>

      <Card title="Metrics timeline">
        <div className={styles.chartRow}>
          <ResponsiveContainer width="100%" height={240}>
            <LineChart data={chartData}>
              <XAxis dataKey="time" hide />
              <YAxis hide />
              <Tooltip />
              <Line type="monotone" dataKey="temperature" stroke="#2d6a4f" strokeWidth={2} />
              <Line type="monotone" dataKey="humidity" stroke="#40916c" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </Card>

      <Card title="Test results">
        <DataTable
          headers={["Test", "Status", "Duration (ms)", "Message"]}
          rows={data.results.map((result) => [
            result.name,
            <StatusBadge key={result.name} status={result.status} />,
            result.durationMs ?? "-",
            result.message ?? "-"
          ])}
        />
      </Card>

      <Card title="Raw output">
        <div className={styles.logPanel}>
          {data.results
            .flatMap((result) => result.readings)
            .map((reading, idx) => (
              <div key={idx}>{reading.rawLine}</div>
            ))}
        </div>
      </Card>
    </div>
  );
}
