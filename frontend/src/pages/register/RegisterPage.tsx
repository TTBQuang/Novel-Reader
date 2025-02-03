import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import styles from "./RegisterPage.module.css";

const RegisterPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault();
    alert(`Đăng ký với: ${username}`);
  };

  const handleBackToLogin = () => {
    if (location.state?.fromLogin) {
      navigate(-1);
    } else {
      navigate("/login");
    }
  };

  return (
    <div className={styles["register-page"]}>
      <div className={styles["register-container"]}>
        <h1 className={styles["register-text"]}>Đăng ký</h1>

        <form className={styles["register-form"]} onSubmit={handleRegister}>
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

          <button type="submit" className={styles["register-btn"]}>
            Đăng ký
          </button>
        </form>

        <div className={styles["login-link"]}>
          <span>Đã có tài khoản?</span>
          <span onClick={handleBackToLogin}> Đăng nhập</span>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
