import { ReactNode } from "react";
import styles from "./DataTable.module.css";

export function DataTable({
  headers,
  rows
}: {
  headers: string[];
  rows: ReactNode[][];
}) {
  return (
    <div className={styles.tableWrapper}>
      <table className={styles.table}>
        <thead>
          <tr>
            {headers.map((h) => (
              <th key={h}>{h}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={index}>{row.map((cell, idx) => <td key={idx}>{cell}</td>)}</tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
