import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/login/LoginPage";
import HomePage from "./pages/home/HomePage";
import RegisterPage from "./pages/register/RegisterPage";
import NovelDetailPage from "./pages/novel-detail/NovelDetailPage";
import ChapterDetailPage from "./pages/chapter-detail/ChapterDetailPage";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/novel/:novelId" element={<NovelDetailPage />} />
        <Route
          path="/novel/:novelId/chapter/:chapterId"
          element={<ChapterDetailPage />}
        />
      </Routes>
    </Router>
  );
};

export default App;
