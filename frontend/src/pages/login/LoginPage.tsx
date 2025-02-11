import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { GoogleLogin } from "@react-oauth/google";
import { GoogleOAuthProvider } from "@react-oauth/google";
import styles from "./LoginPage.module.css";

const GOOGLE_CLIENT_ID =
  "820518618025-8738r4g16qbissmiilcc2ru1f30gfmjb.apps.googleusercontent.com"; // Thay bằng client ID của bạn

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

  const handleGoogleSuccess = async (credentialResponse: any) => {
    try {
      // Gửi ID token lên server
      const response = await fetch("http://localhost:8080/auth/google", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          token: credentialResponse.credential,
        }),
      });

      if (response.ok) {
        const data = await response.json();
        // Lưu token vào localStorage hoặc state management
        localStorage.setItem("accessToken", data.accessToken);
        navigate("/"); // Chuyển hướng sau khi đăng nhập thành công
      } else {
        alert("Đăng nhập thất bại");
      }
    } catch (error) {
      console.error("Error during Google login:", error);
      alert("Đăng nhập thất bại");
    }
  };

  return (
    <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
      <div className={styles["login-page"]}>
        <div className={styles["login-container"]}>
          {/* ... các phần code khác giữ nguyên ... */}

          <div className={styles["login-options"]}>
            <GoogleLogin
              onSuccess={handleGoogleSuccess}
              onError={() => {
                console.log("Login Failed");
                alert("Đăng nhập Google thất bại");
              }}
            />
          </div>

          <h2>
            <span onClick={handleBackToHome}>Quay lại trang chủ</span>
          </h2>
        </div>
      </div>
    </GoogleOAuthProvider>
  );
};

export default LoginPage;
