import { Link } from "react-router-dom";
import styles from "./NovelItemComponent.module.css";
import { NovelItemProps } from "../../models/NovelItem";

const NovelItemComponent = ({ novelItem }: NovelItemProps) => {
  return (
    <div className={styles["novel-item"]}>
      <div className={styles["novel-image"]}>
        <Link to={`/novel/${novelItem.id}/chapter/1`}>
          <img src={novelItem.image} alt={novelItem.name} />
        </Link>
        <div className={styles["latest-chapter"]}>
          <Link
            to={`/novel/${novelItem.id}/chapter/1`}
            className={styles["link"]}
          >
            <span>{novelItem.latestChapter}</span>
          </Link>
        </div>
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
