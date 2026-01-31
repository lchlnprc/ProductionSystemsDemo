import styles from "./StatCard.module.css";

export function StatCard({
  label,
  value,
  trend
}: {
  label: string;
  value: string | number;
  trend?: string;
}) {
  return (
    <div className={styles.card}>
      <span className={styles.label}>{label}</span>
      <strong className={styles.value}>{value}</strong>
      {trend && <span className={styles.trend}>{trend}</span>}
    </div>
  );
}
