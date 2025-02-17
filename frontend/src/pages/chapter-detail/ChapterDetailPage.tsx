import { useParams } from "react-router-dom";
import styles from "./ChapterDetailPage.module.css";
import CommentSection from "../../components/shared/CommentSection";
import ChapterDetailSidebar from "../../components/chapter-detail/ChapterDetailSidebar";
import { useChapterDetail } from "../../hooks/useChapterDetail";
import { useChapterComments } from "../../hooks/useChapterComments";
import { useCreateComment } from "../../hooks/useCreateComment";
import { useNovelDetail } from "../../hooks/useNovelDetail";

const ChapterDetailPage = () => {
  const { novelId, chapterId } = useParams<{
    novelId: string;
    chapterId: string;
  }>();
  const parsedChapterId = chapterId ? parseInt(chapterId, 10) : 0;
  const parsedNovelId = novelId ? parseInt(novelId, 10) : null;

  const {
    chapterData,
    isLoading: isChapterLoading,
    error: chapterError,
  } = useChapterDetail(parsedChapterId);

  const {
    novel,
    isLoading: isNovelLoading,
    error: novelError,
  } = useNovelDetail(parsedNovelId);

  const {
    comments,
    totalComments,
    currentPage,
    totalPages,
    isLoading: isCommentsLoading,
    error: commentsError,
    fetchComments,
    addComment,
  } = useChapterComments(parsedChapterId);

  const {
    createComment,
    isLoading: isCreating,
    error: createError,
  } = useCreateComment();

  const handleChapterCommentSubmit = async (commentText: string) => {
    if (!parsedChapterId) return;
    try {
      const newComment = await createComment({
        chapterId: parsedChapterId,
        content: commentText,
      });

      if (newComment === undefined) {
        return;
      }
      addComment(newComment);
    } catch (err) {
      console.error(err);
    }
  };

  if (isChapterLoading || (isNovelLoading && !novel)) {
    return <div>Loading...</div>;
  }

  if (chapterError) {
    return <div>Error loading chapter: {chapterError.message}</div>;
  }

  if (novelError) {
    return <div>Error loading novel: {novelError.message}</div>;
  }

  if (commentsError) {
    return <div>Error loading comments: {commentsError.message}</div>;
  }

  if (!chapterData) {
    return <div>Không tìm thấy dữ liệu chương.</div>;
  }

  return (
    <div className={styles["chapter-detail-container"]}>
      <div className={styles["chapter-detail-content"]}>
        <div className={styles["title"]}>{chapterData.chapterGroupName}</div>
        <div className={styles["sub-title"]}>{chapterData.name}</div>
        <div className={styles["info"]}>
          {totalComments} Bình luận - Độ dài:{" "}
          {chapterData.wordsCount.toLocaleString()} từ - Cập nhật:{" "}
          {new Date(chapterData.creationDate).toLocaleDateString()}
        </div>
        <div className={styles["content"]}>
          <div
            className={styles["html-content"]}
            dangerouslySetInnerHTML={{ __html: chapterData.content }}
          ></div>
          <div className={styles["comment-section"]}>
            <CommentSection
              comments={comments}
              totalComments={totalComments}
              currentPage={currentPage}
              totalPages={totalPages}
              isLoading={isCommentsLoading || isCreating}
              onPageChange={fetchComments}
              onSubmit={handleChapterCommentSubmit}
            />
            {createError && (
              <div>Error creating comment: {createError.message}</div>
            )}
          </div>
        </div>
        {novel !== null && <ChapterDetailSidebar novel={novel} />}
      </div>
    </div>
  );
};

export default ChapterDetailPage;
