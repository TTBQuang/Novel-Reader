import { FaBackward, FaHome, FaSun, FaMoon, FaForward } from "react-icons/fa";
import styles from "./ChapterDetailSidebar.module.css";
import { useTheme } from "../../hooks/useTheme";
import { useNavigate, useParams } from "react-router-dom";
import { useCallback } from "react";
import { Novel } from "../../models/Novel";

interface ChapterDetailSidebarProps {
  novel: Novel;
}

const ChapterDetailSidebar = ({ novel }: ChapterDetailSidebarProps) => {
  const navigate = useNavigate();
  const { chapterId } = useParams();
  const { theme, changeTheme } = useTheme();

  const findCurrentChapterInfo = useCallback(() => {
    if (!novel?.chapterGroups || !chapterId) return null;

    const currentChapterId = parseInt(chapterId);

    for (
      let groupIndex = 0;
      groupIndex < novel.chapterGroups.length;
      groupIndex++
    ) {
      const group = novel.chapterGroups[groupIndex];
      for (
        let chapterIndex = 0;
        chapterIndex < group.chapters.length;
        chapterIndex++
      ) {
        if (group.chapters[chapterIndex].id === currentChapterId) {
          return {
            groupIndex,
            chapterIndex,
            group,
            chapter: group.chapters[chapterIndex],
          };
        }
      }
    }
    return null;
  }, [novel, chapterId]);

  const navigateToChapter = useCallback(
    (targetChapterId: number) => {
      if (!novel?.id) return;

      navigate(`/novel/${novel.id}/chapter/${targetChapterId}`, {
        state: { novelData: novel },
      });
    },
    [novel, navigate]
  );

  const navigateToNextChapter = useCallback(() => {
    if (!novel) return;

    const currentInfo = findCurrentChapterInfo();
    if (!currentInfo) return;

    const { groupIndex, chapterIndex, group } = currentInfo;

    if (chapterIndex === group.chapters.length - 1) {
      if (groupIndex < novel.chapterGroups.length - 1) {
        const nextGroup = novel.chapterGroups[groupIndex + 1];
        navigateToChapter(nextGroup.chapters[0].id);
      }
    } else {
      navigateToChapter(group.chapters[chapterIndex + 1].id);
    }
  }, [novel, findCurrentChapterInfo, navigateToChapter]);

  const navigateToPreviousChapter = useCallback(() => {
    if (!novel) return;

    const currentInfo = findCurrentChapterInfo();
    if (!currentInfo) return;

    const { groupIndex, chapterIndex, group } = currentInfo;

    if (chapterIndex === 0) {
      if (groupIndex > 0) {
        const prevGroup = novel.chapterGroups[groupIndex - 1];
        navigateToChapter(prevGroup.chapters[prevGroup.chapters.length - 1].id);
      }
    } else {
      navigateToChapter(group.chapters[chapterIndex - 1].id);
    }
  }, [novel, findCurrentChapterInfo, navigateToChapter]);

  const handleHomeClick = useCallback(() => {
    if (novel?.id) {
      navigate(`/novel/${novel.id}`);
    }
  }, [novel, navigate]);

  return (
    <div className={styles["fixed-column"]}>
      <div
        className={styles["icon"]}
        onClick={navigateToPreviousChapter}
        role="button"
        aria-label="Previous chapter"
      >
        <FaBackward />
      </div>
      <div
        className={styles["icon"]}
        onClick={handleHomeClick}
        role="button"
        aria-label="Home"
      >
        <FaHome />
      </div>
      <div
        className={styles["icon"]}
        onClick={changeTheme}
        role="button"
        aria-label="Toggle theme"
      >
        {theme === "light" ? <FaSun /> : <FaMoon />}
      </div>
      <div
        className={styles["icon"]}
        onClick={navigateToNextChapter}
        role="button"
        aria-label="Next chapter"
      >
        <FaForward />
      </div>
    </div>
  );
};

export default ChapterDetailSidebar;
