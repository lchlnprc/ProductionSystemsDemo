import styles from "./AboutPage.module.css";

export function AboutPage() {
  return (
    <div className={styles.page}>
      <section className={styles.section}>
        <h2>System architecture</h2>
        <p>
          This platform validates Arduino-based hardware devices on a production line. The test runner
          streams sensor data, validates stability and ranges, then posts structured results to a
          Spring Boot API backed by PostgreSQL.
        </p>
        <div className={styles.diagram}>
          <img src="/images/SystemOverview.png" alt="System Architecture Overview" />
        </div>
      </section>
      <section className={styles.section}>
        <h2>Motivation</h2>
        <p>
          Manufacturing teams need clear, real-time visibility into device quality. This dashboard
          provides calm, minimal observability for operators and engineering teams.
        </p>
      </section>
      <section className={styles.section}>
        <h2>Tech stack</h2>
        <ul>
          <li>React 18 + TypeScript + Vite</li>
          <li>Redux Toolkit + RTK Query</li>
          <li>Recharts for telemetry visualization</li>
          <li>Spring Boot + PostgreSQL backend</li>
        </ul>
      </section>
    </div>
  );
}
