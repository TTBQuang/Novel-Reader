import { Link } from "react-router-dom";
import ThemeToggle from "./ThemeToggle";
import styles from "./TopAppBar.module.css";
import Logo from "../../assets/logo.png";

const TopAppBar = () => {
  return (
    <header className={styles["top-app-bar"]}>
      <div className={styles["top-app-bar-content"]}>
        <div className={styles["logo-container"]}>
          <Link to="/">
            <img src={Logo} alt="Logo" className={styles["app-logo"]} />
          </Link>
        </div>

        <div className={styles["top-app-bar-content-right"]}>
          <div className={styles["search-container"]}>
            <input
              type="text"
              className={styles["search-bar"]}
              placeholder="Tìm kiếm..."
            />
          </div>
          <ThemeToggle />
          <Link
            to="/login"
            state={{ fromHome: true }}
            className={styles["login-text"]}
          >
            Đăng nhập
          </Link>
        </div>
      </div>
    </header>
  );
};

export default TopAppBar;
