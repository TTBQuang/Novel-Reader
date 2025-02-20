import React, { useState, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify"; // ✅ Import toast
import "react-toastify/dist/ReactToastify.css";
import styles from "./OTPVerification.module.css";
import useVerifyOtpRegistration from "../../hooks/useVerifyOtpRegistration";

const OTPVerificationPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [otp, setOtp] = useState(Array(6).fill(""));
  const [error, setError] = useState("");
  const inputsRef = useRef<(HTMLInputElement | null)[]>([]);
  const { verifyOtp, isVerifying } = useVerifyOtpRegistration();

  const email = location.state?.email || "";

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    const value = e.target.value;
    if (value && isNaN(Number(value))) return;
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);
    if (value && index < 5) {
      inputsRef.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (
    e: React.KeyboardEvent<HTMLInputElement>,
    index: number
  ) => {
    if (e.key === "Backspace" && otp[index] === "" && index > 0) {
      inputsRef.current[index - 1]?.focus();
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    const enteredOTP = otp.join("");
    if (enteredOTP.length < 6) {
      setError("Vui lòng nhập đầy đủ 6 chữ số OTP.");
      return;
    }

    try {
      const isValid = await verifyOtp(enteredOTP, email);
      if (isValid) {
        toast.success("Xác thực OTP thành công! Đang chuyển hướng...");
        setTimeout(() => {
          navigate("/login");
        }, 1000);
      }
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : "Có lỗi xảy ra";
      setError(errorMessage);
    }
  };

  return (
    <div className={styles["otp-verification-container"]}>
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
      <h1 className={styles["otp-verification-title"]}>Xác thực OTP</h1>
      <p>
        Vui lòng nhập mã OTP đã được gửi đến email:{" "}
        <strong>{location.state?.email || "N/A"}</strong>
      </p>
      <form
        className={styles["otp-verification-otp-form"]}
        onSubmit={handleSubmit}
      >
        <div className={styles["otp-verification-input-group"]}>
          {otp.map((digit, index) => (
            <input
              key={index}
              type="text"
              maxLength={1}
              value={digit}
              onChange={(e) => handleChange(e, index)}
              onKeyDown={(e) => handleKeyDown(e, index)}
              ref={(el) => (inputsRef.current[index] = el)}
              className={styles["otp-verification-input"]}
            />
          ))}
        </div>
        {error && (
          <span className={styles["otp-verification-error"]}>{error}</span>
        )}
        <button type="submit" className={styles["otp-verification-submit"]}>
          {isVerifying ? "Đang xác thực..." : "Xác thực OTP"}
        </button>
      </form>
    </div>
  );
};

export default OTPVerificationPage;
