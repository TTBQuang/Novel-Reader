import { Link } from "react-router-dom";
import { useState, useCallback, memo } from "react";
import ThemeToggle from "./ThemeToggle";
import styles from "./TopAppBar.module.css";
import Logo from "../../assets/logo.png";

interface TopAppBarProps {
  onSearch: (keyword: string) => void;
  onLogoClick: () => void;
}

const TopAppBar = memo(({ onSearch, onLogoClick }: TopAppBarProps) => {
  const [searchValue, setSearchValue] = useState("");

  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.key === "Enter") {
        onSearch(searchValue.trim());
      }
    },
    [searchValue, onSearch]
  );

  const handleSearchChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      setSearchValue(e.target.value);
    },
    []
  );

  return (
    <header className={styles["top-app-bar"]}>
      <div className={styles["top-app-bar-content"]}>
        <div className={styles["logo-container"]}>
          <Link to="/" onClick={onLogoClick}>
            <img src={Logo} alt="Logo" className={styles["app-logo"]} />
          </Link>
        </div>
        <div className={styles["top-app-bar-content-right"]}>
          <div className={styles["search-container"]}>
            <input
              type="text"
              className={styles["search-bar"]}
              placeholder="Tìm kiếm..."
              value={searchValue}
              onChange={handleSearchChange}
              onKeyDown={handleKeyDown}
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
});

TopAppBar.displayName = "TopAppBar";

export default TopAppBar;
