import { ReactNode } from "react";
import { Sidebar } from "../Sidebar/Sidebar";
import { TopBar } from "../TopBar/TopBar";
import styles from "./Layout.module.css";

export function Layout({ children }: { children: ReactNode }) {
  return (
    <div className={styles.shell}>
      <Sidebar />
      <div className={styles.content}>
        <TopBar />
        <main className={styles.main}>{children}</main>
      </div>
    </div>
  );
}
