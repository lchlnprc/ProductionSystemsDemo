import { ReactNode } from "react";
import styles from "./ChartCard.module.css";

export function ChartCard({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className={styles.card}>
      <div className={styles.header}>{title}</div>
      <div className={styles.content}>{children}</div>
    </div>
  );
}
