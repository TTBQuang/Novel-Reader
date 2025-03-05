import { Link, useLocation, useNavigate } from "react-router-dom";
import { useLogin } from "../../hooks/useLogin";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import styles from "./LoginPage.module.css";
import { useEffect, useState } from "react";
import { saveTokens } from "../../services/auth";
import { useAuth } from "../../hooks/useAuth";
import { useLoginGoogle } from "../../hooks/useLoginGoogle";

interface CredentialResponse {
  credential: string;
  select_by: string;
  client_id: string;
}

interface GoogleButtonConfig {
  type: "standard" | "icon";
  size: "large" | "medium" | "small";
  theme: "outline" | "filled_blue" | "filled_black";
  text: "signin_with" | "signup_with" | "continue_with" | "signin";
  shape: "rectangular" | "pill" | "circle" | "square";
  logo_alignment: "left" | "center";
}

interface Google {
  accounts: {
    id: {
      initialize: (config: {
        client_id: string;
        callback: (response: CredentialResponse) => void;
      }) => void;
      renderButton: (
        element: HTMLElement | null,
        config: GoogleButtonConfig
      ) => void;
    };
  };
}

declare global {
  interface Window {
    google?: Google;
    handleCredentialResponse?: (response: CredentialResponse) => void;
  }
}

const LoginPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { setUser } = useAuth();
  const { formData, errors, isLoading, handleInputChange, handleSubmit } =
    useLogin();
  const { loginGoogle } = useLoginGoogle();
  const [rememberMe, setRememberMe] = useState(false);

  useEffect(() => {
    const script = document.createElement("script");
    script.src = "https://accounts.google.com/gsi/client";
    script.async = true;
    script.defer = true;
    document.body.appendChild(script);

    script.onload = () => {
      if (window.google?.accounts) {
        window.google.accounts.id.initialize({
          client_id: import.meta.env.VITE_GOOGLE_CLIENT_ID || "",
          callback: handleCredentialResponse,
        });

        const buttonElement = document.getElementById("googleSignInDiv");
        if (buttonElement) {
          window.google.accounts.id.renderButton(buttonElement, {
            type: "standard",
            size: "large",
            theme: "outline",
            text: "signin_with",
            shape: "rectangular",
            logo_alignment: "left",
          });
        }
      }
    };

    return () => {
      const scriptElement = document.querySelector(
        'script[src="https://accounts.google.com/gsi/client"]'
      );
      if (scriptElement && scriptElement.parentNode) {
        scriptElement.parentNode.removeChild(scriptElement);
      }
    };
  }, []);

  const handleCredentialResponse = async (response: CredentialResponse) => {
    const loginResponse = await loginGoogle(response.credential);

    if (loginResponse) {
      saveTokens(loginResponse.token, true);
      setUser(loginResponse.user);

      toast.success("Đăng nhập Google thành công!");
      setTimeout(() => {
        navigate(-1);
      }, 500);
    } else {
      toast.error("Đăng nhập Google thất bại!");
    }
  };

  if (typeof window !== "undefined") {
    window.handleCredentialResponse = handleCredentialResponse;
  }

  const handleBackToHome = () => {
    const from = location.state?.from || "/";
    navigate(from);
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    const loginResponse = await handleSubmit();
    saveTokens(loginResponse.token, rememberMe);
    setUser(loginResponse.user);

    toast.success("Đăng nhập thành công!");
    setTimeout(() => {
      const from = location.state?.from || "/";
      navigate(from);
    }, 500);
  };

  return (
    <div className={styles["login-page"]}>
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

      <div className={styles["login-container"]}>
        <h1 className={styles["login-text"]}>Đăng nhập</h1>

        <form className={styles["login-form"]} onSubmit={handleLogin}>
          <div className={styles["input-group"]}>
            <label htmlFor="username">Tên đăng nhập</label>
            <input
              type="text"
              id="username"
              value={formData.username}
              onChange={handleInputChange}
              required
              placeholder="Nhập tên đăng nhập"
            />
            {errors.username && (
              <span className={styles["error-message"]}>{errors.username}</span>
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
            />
            {errors.password && (
              <span className={styles["error-message"]}>{errors.password}</span>
            )}
          </div>

          <div className={styles["remember-me"]}>
            <input
              type="checkbox"
              id="rememberMe"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
            />
            <label htmlFor="rememberMe">Ghi nhớ đăng nhập</label>
          </div>

          <button
            type="submit"
            className={styles["login-btn"]}
            disabled={isLoading}
          >
            {isLoading ? "Đang đăng nhập..." : "Đăng nhập"}
          </button>

          <div className={styles["forgot-password-container"]}>
            <Link to="/forgot-password" className={styles["forgot-password"]}>
              Quên mật khẩu?
            </Link>
          </div>
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
          <div id="googleSignInDiv"></div>
        </div>

        <h2>
          <span onClick={handleBackToHome}>Quay lại trang chủ</span>
        </h2>
      </div>
    </div>
  );
};

export default LoginPage;
