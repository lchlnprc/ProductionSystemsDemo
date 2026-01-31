import { NavLink } from "react-router-dom";
import styles from "./Sidebar.module.css";

const links = [
  { to: "/", label: "Dashboard" },
  { to: "/devices", label: "Devices" },
  { to: "/about", label: "About" }
];

export function Sidebar() {
  return (
    <aside className={styles.sidebar}>
      <div className={styles.brand}>Production Ops</div>
      <nav className={styles.nav}>
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              isActive ? `${styles.link} ${styles.active}` : styles.link
            }
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
      <div className={styles.footer}>v1.0</div>
    </aside>
  );
}
