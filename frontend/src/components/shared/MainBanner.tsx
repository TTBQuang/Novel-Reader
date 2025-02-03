import { useTheme } from "../../hooks/useTheme";
import LightBannerImage from "../../assets/banner-light.jpg";
import DarkBannerImage from "../../assets/banner-dark.jpg";
import styles from "./MainBanner.module.css";

const MainBanner = () => {
  const { theme } = useTheme();

  return (
    <div className={styles["banner"]}>
      <div className={styles["banner-image"]}>
        {theme === "light" ? (
          <img src={LightBannerImage} alt="Banner" />
        ) : (
          <img src={DarkBannerImage} alt="Banner" />
        )}
      </div>
    </div>
  );
};

export default MainBanner;
