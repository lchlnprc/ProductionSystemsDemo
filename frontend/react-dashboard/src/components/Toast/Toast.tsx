import { useEffect } from "react";
import styles from "./Toast.module.css";

export function Toast({
  message,
  tone = "success",
  onClose,
  duration = 3000
}: {
  message: string;
  tone?: "success" | "error" | "info";
  onClose: () => void;
  duration?: number;
}) {
  useEffect(() => {
    const timer = setTimeout(onClose, duration);
    return () => clearTimeout(timer);
  }, [onClose, duration]);

  return (
    <div className={`${styles.toast} ${styles[tone]}`}>
      {message}
    </div>
  );
}
