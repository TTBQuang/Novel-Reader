import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/login/LoginPage";
import HomePage from "./pages/home/HomePage";
import RegisterPage from "./pages/register/RegisterPage";
import NovelDetailPage from "./pages/novel-detail/NovelDetailPage";
import ChapterDetailPage from "./pages/chapter-detail/ChapterDetailPage";
import UserManagementPage from "./pages/user-management/UserManagementPage";
import OTPVerificationPage from "./pages/otp-verification/OTPVerificationPage";
import ForgotPasswordPage from "./pages/forgot-password/ForgotPasswordPage";
import UserDetailPage from "./pages/user-detail/UserDetailPage";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/otp-verification" element={<OTPVerificationPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="/novel/:novelId" element={<NovelDetailPage />} />
        <Route
          path="/novel/:novelId/chapter/:chapterId"
          element={<ChapterDetailPage />}
        />
        <Route path="/user/:userId" element={<UserDetailPage />} />
        <Route path="/admin/users" element={<UserManagementPage />} />
      </Routes>
    </Router>
  );
};

export default App;
