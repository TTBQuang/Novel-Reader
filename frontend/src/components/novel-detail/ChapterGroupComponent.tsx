import { Link } from "react-router-dom";
import styles from "./ChapterGroupComponent.module.css";
import { Novel } from "../../models/Novel";
import { ChapterGroup } from "../../models/ChapterGroup";
import { formatDate } from "../../utils/dateUtils";
export interface ChapterGroupComponentProps {
  chapterGroup: ChapterGroup;
  novel: Novel;
}

const ChapterGroupComponent = ({
  chapterGroup,
  novel,
}: ChapterGroupComponentProps) => {
  return (
    <div className={styles["chapter-group"]}>
      <div className={styles["title-row"]}>
        <h3>{chapterGroup.name}</h3>
      </div>

      <div className={styles["content-row"]}>
        <div className={styles["image-column"]}>
          <img src={novel.cover} alt={chapterGroup.name} />
        </div>

        <div className={styles["list-column"]}>
          <ul className={styles["chapter-list"]}>
            {chapterGroup.chapters.map((chapter, index) => (
              <li key={index} className={styles["chapter-item"]}>
                <Link
                  to={`chapter/${chapter.id}`}
                  className={styles["link"]}
                  state={{ novel }}
                >
                  <div className={styles["chapter-title"]}>
                    <span>{chapter.name}</span>
                  </div>
                </Link>

                <span className={styles["chapter-date"]}>
                  {formatDate(chapter.creationDate)}
                </span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default ChapterGroupComponent;
