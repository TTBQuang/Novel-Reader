import { Link } from "react-router-dom";
import styles from "./NovelItemComponent.module.css";
import { NovelItemProps } from "../../models/NovelItem";

const NovelItemComponent = ({ novelItem }: NovelItemProps) => {
  return (
    <div className={styles["novel-item"]}>
      <div className={styles["novel-image"]}>
        <Link to={`/novel/${novelItem.id}`}>
          <img src={novelItem.cover} alt="img" />
        </Link>
      </div>
      <div className={styles["novel-title"]}>
        <Link to={`/novel/${novelItem.id}`} className={styles["link"]}>
          <span>{novelItem.name}</span>
        </Link>
      </div>
    </div>
  );
};

export default NovelItemComponent;
