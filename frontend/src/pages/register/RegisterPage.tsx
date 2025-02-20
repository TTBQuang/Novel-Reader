import { useNavigate, useLocation } from "react-router-dom";
import { useInitiateRegistration } from "../../hooks/useInitiateRegistration";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import styles from "./RegisterPage.module.css";

const RegisterPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { formData, errors, isLoading, handleInputChange, handleSubmit } =
    useInitiateRegistration();

  const handleBackToLogin = () => {
    if (location.state?.fromLogin) {
      navigate(-1);
    } else {
      navigate("/login");
    }
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const success = await handleSubmit();
    if (success) {
      navigate("/otp-verification", { state: { email: formData.email } });
    }
  };

  return (
    <div className={styles["register-page"]}>
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />

      <div className={styles["register-container"]}>
        <h1 className={styles["register-text"]}>Đăng ký</h1>

        <form
          className={styles["register-form"]}
          onSubmit={onSubmit}
          autoComplete="off"
        >
          <div className={styles["input-group"]}>
            <label htmlFor="username">Tên đăng nhập</label>
            <input
              type="text"
              id="username"
              value={formData.username}
              onChange={handleInputChange}
              required
              placeholder="Nhập tên đăng nhập"
              disabled={isLoading}
              autoComplete="off"
            />
            {errors.username && (
              <span className={styles["error-message"]}>{errors.username}</span>
            )}
          </div>

          <div className={styles["input-group"]}>
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              value={formData.email}
              onChange={handleInputChange}
              required
              placeholder="Nhập Email"
              disabled={isLoading}
              autoComplete="off"
            />
            {errors.email && (
              <span className={styles["error-message"]}>{errors.email}</span>
            )}
          </div>

          <div className={styles["input-group"]}>
            <label htmlFor="password">Mật khẩu</label>
            <input
              type="password"
              id="password"
              value={formData.password}
              onChange={handleInputChange}
              required
              placeholder="Nhập mật khẩu"
              disabled={isLoading}
              autoComplete="new-password"
            />
            {errors.password && (
              <span className={styles["error-message"]}>{errors.password}</span>
            )}
          </div>

          <button
            type="submit"
            className={styles["register-btn"]}
            disabled={isLoading}
          >
            {isLoading ? "Đang xử lý..." : "Đăng ký"}
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
