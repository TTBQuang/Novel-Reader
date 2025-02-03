import { FaBackward, FaHome, FaSun, FaMoon, FaForward } from "react-icons/fa";
import styles from "./ChapterDetailSidebar.module.css";
import { useTheme } from "../../hooks/useTheme";
import { useNavigate } from "react-router-dom";

const ChapterDetailSidebar = () => {
  const navigate = useNavigate();
  const { theme, changeTheme } = useTheme();

  return (
    <div className={styles["fixed-column"]}>
      <div className={styles["icon"]}>
        <FaBackward />
      </div>
      <div className={styles["icon"]} onClick={() => navigate("/novel/1")}>
        <FaHome />
      </div>
      {theme === "light" ? (
        <div className={styles["icon"]} onClick={() => changeTheme()}>
          <FaSun />
        </div>
      ) : (
        <div className={styles["icon"]} onClick={() => changeTheme()}>
          <FaMoon />
        </div>
      )}
      <div className={styles["icon"]}>
        <FaForward />
      </div>
    </div>
  );
};

export default ChapterDetailSidebar;
