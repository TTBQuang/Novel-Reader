import { FaSun, FaMoon } from "react-icons/fa";
import { useTheme } from "../../hooks/useTheme";
import styles from "./ThemeToggle.module.css";

const ThemeToggle = () => {
  const { theme, changeTheme: setTheme } = useTheme();

  const handleToggle = () => {
    setTheme();
  };

  return (
    <label className={styles["switch"]}>
      <input
        type="checkbox"
        checked={theme === "light"}
        onChange={handleToggle}
      />
      <span className={styles["slider"]}>
        {theme === "light" ? (
          <FaSun className={styles["icon"]} style={{ marginRight: "24px" }} />
        ) : (
          <FaMoon className={styles["icon"]} style={{ marginLeft: "24px" }} />
        )}
      </span>
    </label>
  );
};

export default ThemeToggle;
