import { Link } from "react-router-dom";
import styles from "./ChapterGroupComponent.module.css";

export interface Chapter {
  title: string;
  date: string;
}

export interface ChapterGroupComponentProps {
  groupTitle: string;
  imageSrc: string;
  chapters: Chapter[];
}

const ChapterGroupComponent = ({
  groupTitle,
  imageSrc,
  chapters,
}: ChapterGroupComponentProps) => {
  return (
    <div className={styles["chapter-group"]}>
      <div className={styles["title-row"]}>
        <h2>{groupTitle}</h2>
      </div>

      <div className={styles["content-row"]}>
        <div className={styles["image-column"]}>
          <img src={imageSrc} alt={groupTitle} />
        </div>

        <div className={styles["list-column"]}>
          <ul className={styles["chapter-list"]}>
            {chapters.map((chapter, index) => (
              <li key={index} className={styles["chapter-item"]}>
                <Link to="chapter/1" className={styles["link"]}>
                  <div className={styles["chapter-title"]}>
                    <span>{chapter.title}</span>
                  </div>
                </Link>
                <span className={styles["chapter-date"]}>{chapter.date}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default ChapterGroupComponent;
