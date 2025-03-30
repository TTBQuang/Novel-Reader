import { Novel } from "../../models/Novel";
import { mapApiStatusToNovelStatus } from "../../models/NovelStatus";
import styles from "./NovelDetail.module.css";

interface NovelDetailProps {
  novel: Novel;
}

const NovelDetail = ({ novel }: NovelDetailProps) => {
  return (
    <div className={styles["novel-container"]}>
      <div className={styles["novel-header"]}>
        <img
          src={novel.cover}
          alt={novel.name}
          className={styles["novel-image"]}
        />
        <div className={styles["novel-info"]}>
          <h1 className={styles["novel-title"]}>{novel.name}</h1>
          <div className={styles["novel-genres"]}>
            {novel.genres.map((genre, index) => (
              <span key={index} className={styles["novel-genre"]}>
                {genre.name}
              </span>
            ))}
          </div>
          <div className={styles["novel-metadata"]}>
            <p>
              <strong>Tác giả:</strong>{" "}
              <span>{novel.author?.trim() || "N/A"}</span>
            </p>
            <p>
              <strong>Họa sĩ:</strong>{" "}
              <span>{novel.artist?.trim() || "N/A"}</span>
            </p>

            <p>
              <strong>Tình trạng:</strong>{" "}
              <span>{mapApiStatusToNovelStatus(novel.status)}</span>
            </p>
          </div>
        </div>
      </div>
      <div className={styles["novel-summary"]}>
        <h2 className={styles["summary-title"]}>Tóm tắt</h2>
        <p className={styles["summary-content"]}>{novel.summary}</p>
      </div>
    </div>
  );
};

export default NovelDetail;
