import { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import styles from "./ForgotPasswordPage.module.css";
import { usePasswordResetInitiate } from "../../hooks/usePasswordResetInitiate";
import usePasswordResetConfirm from "../../hooks/usePasswordResetConfirm";

const ForgotPasswordPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [showResetForm, setShowResetForm] = useState(false);
  const [otp, setOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const { resetPassword, isLoading } = usePasswordResetInitiate();
  const { verifyOtp, isVerifying } = usePasswordResetConfirm();

  const handleInitResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    const success = await resetPassword(email);
    if (success) {
      setShowResetForm(true);
    }
  };

  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();

    if (newPassword !== confirmPassword) {
      toast.error("Mật khẩu xác nhận không khớp!");
      return;
    }

    try {
      const success = await verifyOtp(otp, email, newPassword);
      if (success) {
        toast.success("Đặt lại mật khẩu thành công! Đang chuyển hướng...");
        setTimeout(() => {
          navigate("/login");
        }, 1000);
      }
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      } else {
        toast.error("Có lỗi xảy ra khi đặt lại mật khẩu!");
      }
    }
  };

  return (
    <div className={styles["forgot-password-page"]}>
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
      <div className={styles["forgot-password-container"]}>
        {!showResetForm ? (
          <>
            <h1>Quên mật khẩu</h1>
            <p>Nhập email của bạn để đặt lại mật khẩu</p>
            <form onSubmit={handleInitResetPassword}>
              <input
                type="email"
                placeholder="Nhập email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
              <button type="submit" disabled={isLoading}>
                {isLoading ? "Đang xử lý..." : "Gửi yêu cầu"}
              </button>
            </form>
          </>
        ) : (
          <>
            <h1>Đặt lại mật khẩu</h1>
            <p>
              Vui lòng nhập mã OTP đã được gửi đến email của bạn và mật khẩu mới
            </p>
            <form onSubmit={handleResetPassword}>
              <input
                type="text"
                placeholder="Nhập mã OTP"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                required
              />
              <input
                type="password"
                placeholder="Nhập mật khẩu mới"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
              />
              <input
                type="password"
                placeholder="Xác nhận mật khẩu mới"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
              <button type="submit" disabled={isVerifying}>
                {isVerifying ? "Đang xử lý..." : "Đặt lại mật khẩu"}
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
};

export default ForgotPasswordPage;
