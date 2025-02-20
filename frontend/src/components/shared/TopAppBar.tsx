import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState, useCallback, memo, useRef, useEffect } from "react";
import ThemeToggle from "./ThemeToggle";
import styles from "./TopAppBar.module.css";
import Logo from "../../assets/logo.png";
import { FaCaretDown } from "react-icons/fa";
import { useAuth } from "../../hooks/useAuth";

interface TopAppBarProps {
  onSearch: (keyword: string) => void;
  onLogoClick: () => void;
}

const TopAppBar = memo(({ onSearch, onLogoClick }: TopAppBarProps) => {
  const [searchValue, setSearchValue] = useState("");
  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const { user, isAuthenticated, logout, isLoading } = useAuth();

  const dropdownRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();
  const location = useLocation();
  const isAdminPage = location.pathname.includes("admin");

  const toggleDropdown = useCallback(() => {
    setDropdownOpen((prev) => !prev);
  }, []);

  const handleLogout = useCallback(() => {
    logout();
    setDropdownOpen(false);
    if (location.pathname.includes("admin")) {
      navigate("/");
    }
  }, [logout, location.pathname, navigate]);

  const handleAdminPageClick = useCallback(() => {
    navigate("/admin/users");
    setDropdownOpen(false);
  }, []);

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

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const renderAuthSection = () => {
    if (isLoading) {
      return null;
    }

    if (isAuthenticated && user) {
      return (
        <div className={styles["user-container"]} ref={dropdownRef}>
          <span className={styles["username"]}>{user.username}</span>
          <span className={styles["dropdown-icon"]} onClick={toggleDropdown}>
            <FaCaretDown />
          </span>
          {isDropdownOpen && (
            <div className={styles["dropdown-menu"]}>
              {user.admin && (
                <div
                  className={styles["dropdown-item"]}
                  onClick={handleAdminPageClick}
                >
                  Quản lý user
                </div>
              )}
              <div className={styles["dropdown-item"]} onClick={handleLogout}>
                Đăng xuất
              </div>
            </div>
          )}
        </div>
      );
    }

    return (
      <Link
        to="/login"
        state={{ from: location.pathname + location.search }}
        className={styles["login-text"]}
      >
        Đăng nhập
      </Link>
    );
  };

  return (
    <header className={styles["top-app-bar"]}>
      <div className={styles["top-app-bar-content"]}>
        <div className={styles["logo-container"]}>
          <Link to="/" onClick={onLogoClick}>
            <img src={Logo} alt="Logo" className={styles["app-logo"]} />
          </Link>
        </div>
        <div className={styles["top-app-bar-content-right"]}>
          {!isAdminPage && (
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
          )}
          <ThemeToggle />
          {renderAuthSection()}
        </div>
      </div>
    </header>
  );
});

export default TopAppBar;
