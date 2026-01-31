import styles from "./TopBar.module.css";

export function TopBar() {
  return (
    <header className={styles.topbar}>
      <div>
        <h1>Manufacturing Test Dashboard</h1>
        <p>Live production health and test telemetry</p>
      </div>
      <div className={styles.status}>
        <span className={styles.dot} />
        Live
      </div>
    </header>
  );
}
