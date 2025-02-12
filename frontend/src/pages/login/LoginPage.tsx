import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import styles from "./LoginPage.module.css";

const LoginPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(false);

  const handleBackToHome = () => {
    if (location.state?.fromHome) {
      navigate(-1);
    } else {
      navigate("/");
    }
  };

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    alert(`Đăng nhập với: ${username}`);
  };

  return (
    <div className={styles["login-page"]}>
      <div className={styles["login-container"]}>
        <h1 className={styles["login-text"]}>Đăng nhập</h1>

        <form className={styles["login-form"]} onSubmit={handleLogin}>
          <div className={styles["input-group"]}>
            <label htmlFor="username">Tên đăng nhập</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              placeholder="Nhập tên đăng nhập"
            />
          </div>

          <div className={styles["input-group"]}>
            <label htmlFor="password">Mật khẩu</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="Nhập mật khẩu"
            />
          </div>

          <div className={styles["remember-me"]}>
            <input
              type="checkbox"
              id="rememberMe"
              checked={rememberMe}
              onChange={() => setRememberMe(!rememberMe)}
            />
            <label htmlFor="rememberMe">Ghi nhớ đăng nhập</label>
          </div>

          <button type="submit" className={styles["login-btn"]}>
            Đăng nhập
          </button>
        </form>

        <div className={styles["register-link"]}>
          <span>Chưa có tài khoản?</span>
          <Link
            to="/register"
            state={{ fromLogin: true }}
            className={styles["register-link-text"]}
          >
            Đăng ký
          </Link>
        </div>

        <div className={styles["separator"]}>Hoặc</div>

        <div className={styles["login-options"]}>
          <button className={styles["google-login-btn"]}>
            Đăng nhập với Google
          </button>
        </div>

        <h2>
          <span>
            <span onClick={handleBackToHome}>Quay lại trang chủ</span>
          </span>
        </h2>
      </div>
    </div>
  );
};

export default LoginPage;
