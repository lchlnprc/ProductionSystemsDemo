import styles from "./StatusBadge.module.css";

export function StatusBadge({
  status
}: {
  status: "passed" | "failed" | "running" | "ok" | "warning" | "critical";
}) {
  return <span className={`${styles.badge} ${styles[status]}`}>{status}</span>;
}
