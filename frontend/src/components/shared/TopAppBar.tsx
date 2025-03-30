import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState, useCallback, memo, useRef, useEffect } from "react";
import ThemeToggle from "./ThemeToggle";
import styles from "./TopAppBar.module.css";
import Logo from "../../assets/logo.png";
import { FaCaretDown, FaSearch, FaBars, FaTimes } from "react-icons/fa";
import { useAuth } from "../../hooks/useAuth";

interface TopAppBarProps {
  onSearch: (keyword: string) => void;
  onLogoClick: () => void;
}

const TopAppBar = memo(({ onSearch, onLogoClick }: TopAppBarProps) => {
  const [searchValue, setSearchValue] = useState("");
  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [isMobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [isSearchOpen, setSearchOpen] = useState(false);
  const { user, isAuthenticated, logout, isLoading } = useAuth();

  const dropdownRef = useRef<HTMLDivElement>(null);
  const searchInputRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const location = useLocation();
  const isAdminPage = location.pathname.includes("admin");

  const toggleDropdown = useCallback(() => {
    setDropdownOpen((prev) => !prev);
  }, []);

  const toggleMobileMenu = useCallback(() => {
    setMobileMenuOpen((prev) => !prev);
    // Close search when menu is opened
    if (!isMobileMenuOpen) {
      setSearchOpen(false);
    }
  }, [isMobileMenuOpen]);

  const toggleSearch = useCallback(() => {
    setSearchOpen((prev) => !prev);
    // Focus the search input when opened
    if (!isSearchOpen && searchInputRef.current) {
      setTimeout(() => {
        searchInputRef.current?.focus();
      }, 100);
    }
  }, [isSearchOpen]);

  const handleLogout = useCallback(() => {
    logout();
    setDropdownOpen(false);
    setMobileMenuOpen(false);
    if (location.pathname.includes("admin")) {
      navigate("/");
    }
  }, [logout, location.pathname, navigate]);

  const handleAdminPageClick = useCallback(() => {
    navigate("/admin/users");
    setDropdownOpen(false);
    setMobileMenuOpen(false);
  }, [navigate]);

  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.key === "Enter") {
        onSearch(searchValue.trim());
        // Close search bar on mobile after search
        if (window.innerWidth <= 768) {
          setSearchOpen(false);
        }
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

  const navigateToProfile = () => {
    navigate(`/user/${user?.id}`);
    setDropdownOpen(false);
    setMobileMenuOpen(false);
  };

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

  // Close mobile menu on window resize (if screen becomes larger)
  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth > 768 && isMobileMenuOpen) {
        setMobileMenuOpen(false);
      }
      if (window.innerWidth > 768 && isSearchOpen) {
        setSearchOpen(false);
      }
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [isMobileMenuOpen, isSearchOpen]);

  const renderAuthSection = () => {
    if (isLoading) {
      return null;
    }

    if (isAuthenticated && user) {
      return (
        <div className={styles["user-container"]} ref={dropdownRef}>
          <span className={styles["display_name"]}>{user.displayName}</span>
          <span className={styles["dropdown-icon"]} onClick={toggleDropdown}>
            <FaCaretDown />
          </span>
          {isDropdownOpen && (
            <div className={styles["dropdown-menu"]}>
              <div
                className={styles["dropdown-item"]}
                onClick={navigateToProfile}
              >
                Trang cá nhân
              </div>

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

  // Mobile menu content
  const renderMobileMenu = () => {
    if (!isMobileMenuOpen) return null;

    return (
      <div className={styles["mobile-menu"]}>
        {!isAdminPage && (
          <div className={styles["mobile-search-container"]}>
            <input
              type="text"
              className={styles["mobile-search-bar"]}
              placeholder="Tìm kiếm..."
              value={searchValue}
              onChange={handleSearchChange}
              onKeyDown={handleKeyDown}
            />
          </div>
        )}
        <div className={styles["mobile-menu-items"]}>
          {isAuthenticated && user ? (
            <>
              <div
                className={styles["mobile-menu-item"]}
                onClick={navigateToProfile}
              >
                Trang cá nhân
              </div>
              {user.admin && (
                <div
                  className={styles["mobile-menu-item"]}
                  onClick={handleAdminPageClick}
                >
                  Quản lý user
                </div>
              )}
              <div
                className={styles["mobile-menu-item"]}
                onClick={handleLogout}
              >
                Đăng xuất
              </div>
            </>
          ) : (
            <Link
              to="/login"
              state={{ from: location.pathname + location.search }}
              className={styles["mobile-menu-item"]}
              onClick={() => setMobileMenuOpen(false)}
            >
              Đăng nhập
            </Link>
          )}
        </div>
      </div>
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

        {/* Desktop view */}
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
          <div className={styles["desktop-controls"]}>
            <ThemeToggle />
            {renderAuthSection()}
          </div>
        </div>

        {/* Mobile view controls */}
        <div className={styles["mobile-controls"]}>
          {!isAdminPage && (
            <button
              className={styles["mobile-icon-button"]}
              onClick={toggleSearch}
              aria-label="Search"
            >
              {isSearchOpen ? <FaTimes /> : <FaSearch />}
            </button>
          )}
          <ThemeToggle />
          <button
            className={styles["mobile-icon-button"]}
            onClick={toggleMobileMenu}
            aria-label="Menu"
          >
            {isMobileMenuOpen ? <FaTimes /> : <FaBars />}
          </button>
        </div>

        {/* Mobile search bar */}
        {isSearchOpen && !isAdminPage && (
          <div className={styles["mobile-search-overlay"]}>
            <input
              ref={searchInputRef}
              type="text"
              className={styles["mobile-search-overlay-input"]}
              placeholder="Tìm kiếm..."
              value={searchValue}
              onChange={handleSearchChange}
              onKeyDown={handleKeyDown}
            />
          </div>
        )}
      </div>

      {/* Mobile menu */}
      {renderMobileMenu()}
    </header>
  );
});

export default TopAppBar;
