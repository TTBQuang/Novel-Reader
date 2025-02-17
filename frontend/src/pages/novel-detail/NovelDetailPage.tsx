import { useParams, useNavigate } from "react-router-dom";
import TopAppBar from "../../components/shared/TopAppBar";
import MainBanner from "../../components/shared/MainBanner";
import NovelDetail from "../../components/novel-detail/NovelDetail";
import ChapterGroupComponent from "../../components/novel-detail/ChapterGroupComponent";
import CommentSection from "../../components/shared/CommentSection";
import styles from "./NovelDetailPage.module.css";
import { useNovelDetail } from "../../hooks/useNovelDetail";
import { useNovelComments } from "../../hooks/useNovelComments";
import { useCreateComment } from "../../hooks/useCreateComment";
import avatar from "../../assets/avatar.jpg";
import { ChapterGroup } from "../../models/ChapterGroup";

const NovelDetailPage = () => {
  const { novelId } = useParams<{ novelId: string }>();
  const parsedNovelId = novelId ? parseInt(novelId, 10) : null;
  const navigate = useNavigate();

  const { novel, isLoading, error } = useNovelDetail(parsedNovelId);

  const {
    comments: novelComments,
    totalComments: novelTotalComments,
    currentPage: novelCurrentPage,
    totalPages: novelTotalPages,
    isLoading: isNovelCommentsLoading,
    error: novelCommentsError,
    fetchComments: fetchNovelComments,
    addComment: addNovelComment,
  } = useNovelComments(parsedNovelId);

  const {
    createComment,
    isLoading: isCreating,
    error: createError,
  } = useCreateComment();

  const handleNovelCommentSubmit = async (commentText: string) => {
    if (!novel || !novel.id) return;
    try {
      const newComment = await createComment({
        novelId: novel.id,
        content: commentText,
      });
      if (newComment === undefined) {
        return;
      }
      addNovelComment(newComment);
    } catch (err) {
      console.error(err);
    }
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error.message}</div>;
  }

  if (!novel) {
    return <div>Không tìm thấy dữ liệu truyện.</div>;
  }

  return (
    <>
      <TopAppBar
        onSearch={(keyword: string) => {
          navigate(`/?keyword=${encodeURIComponent(keyword)}`);
        }}
        onLogoClick={() => {
          navigate("/");
        }}
      />
      <MainBanner />
      <div className={styles["page-content-container"]}>
        <div className={styles["novel-detail-container"]}>
          <div className={styles["novel-detail"]}>
            <NovelDetail novel={novel} />
          </div>
          <div className={styles["avatar-row"]}>
            <div className={styles["avatar"]}>
              <img src={avatar} alt="Avatar" />
            </div>
            <div className={styles["poster-name"]}>
              <span>{novel.poster.username}</span>
            </div>
          </div>

          {novel.chapterGroups.map((chapterGroup: ChapterGroup) => (
            <div
              key={chapterGroup.id}
              className={styles["chapter-group-container"]}
            >
              <ChapterGroupComponent
                novel={novel}
                chapterGroup={chapterGroup}
              />
            </div>
          ))}

          {novelCommentsError ? (
            <div>Error loading comments: {novelCommentsError.message}</div>
          ) : (
            <CommentSection
              comments={novelComments}
              totalComments={novelTotalComments}
              currentPage={novelCurrentPage}
              totalPages={novelTotalPages}
              isLoading={isNovelCommentsLoading || isCreating}
              onPageChange={fetchNovelComments}
              onSubmit={handleNovelCommentSubmit}
            />
          )}
          {createError && (
            <div>Error creating comment: {createError.message}</div>
          )}
        </div>
      </div>
    </>
  );
};

export default NovelDetailPage;
