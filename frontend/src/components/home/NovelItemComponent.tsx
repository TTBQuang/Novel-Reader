import { Link } from "react-router-dom";
import styles from "./NovelItemComponent.module.css";
import { Novel } from "../../models/Novel";
import { mapApiStatusToNovelStatus } from "../../models/NovelStatus";

export interface NovelItemProps {
  novelItem: Novel;
}

const NovelItemComponent = ({ novelItem }: NovelItemProps) => {
  return (
    <div className={styles["novel-item"]}>
      <div className={styles["novel-image-container"]}>
        <div className={styles["novel-image"]}>
          <Link to={`/novel/${novelItem.id}`}>
            <img src={novelItem.cover} alt="img" />
          </Link>
        </div>
        <div className={styles["summary-overlay"]}>
          <p>Số từ: {novelItem.wordsCount}</p>
          <p>Tình trạng: {mapApiStatusToNovelStatus(novelItem.status)}</p>
          <p>{novelItem.summary}</p>
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
