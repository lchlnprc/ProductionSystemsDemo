import styles from "./Skeleton.module.css";

export function Skeleton({ height = 16 }: { height?: number }) {
  return <div className={styles.skeleton} style={{ height }} />;
}
