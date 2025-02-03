import styles from "./NovelDetail.module.css";

interface NovelDetailProps {
  title: string;
  coverImage: string;
  genres: string[];
  author: string;
  artist: string;
  status: string;
  summary: string;
}

const NovelDetail = ({
  title,
  coverImage,
  genres,
  author,
  artist,
  status,
  summary,
}: NovelDetailProps) => {
  return (
    <div className={styles["novel-container"]}>
      <div className={styles["novel-header"]}>
        <img src={coverImage} alt={title} className={styles["novel-image"]} />
        <div className={styles["novel-info"]}>
          <h1 className={styles["novel-title"]}>{title}</h1>
          <div className={styles["novel-genres"]}>
            {genres.map((genre, index) => (
              <span key={index} className={styles["novel-genre"]}>
                {genre}
              </span>
            ))}
          </div>
          <div className={styles["novel-metadata"]}>
            <p>
              <strong>Tác giả:</strong> <span>{author}</span>
            </p>
            <p>
              <strong>Họa sĩ:</strong> <span>{artist}</span>
            </p>
            <p>
              <strong>Tình trạng:</strong> <span>{status}</span>
            </p>
          </div>
        </div>
      </div>
      <div className={styles["novel-summary"]}>
        <h2 className={styles["summary-title"]}>Tóm tắt</h2>
        <p className={styles["summary-content"]}>{summary}</p>
      </div>
    </div>
  );
};

export default NovelDetail;
