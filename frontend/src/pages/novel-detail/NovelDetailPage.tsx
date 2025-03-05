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
import { useDeleteComment } from "../../hooks/useDeleteComment";
import { ToastContainer } from "react-toastify";

const NovelDetailPage = () => {
  const { novelId } = useParams<{ novelId: string }>();
  const parsedNovelId = novelId ? parseInt(novelId, 10) : null;
  const navigate = useNavigate();

  const {
    novel,
    isLoading: isLoadingNovelDetail,
    error: errorLoadingNovelDetail,
  } = useNovelDetail(parsedNovelId);
  const { handleDeleteComment: deleteCommentInServer } = useDeleteComment();

  const {
    comments: novelComments,
    totalComments: novelTotalComments,
    currentPage: novelCurrentPage,
    totalPages: novelTotalPages,
    isLoading: isNovelCommentsLoading,
    error: novelCommentsError,
    fetchComments: fetchNovelComments,
    addComment: addNovelComment,
    deleteComment: deleteItemInLocalComments,
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

  const deleteComment = async (commentId: number) => {
    try {
      await deleteCommentInServer(commentId);
      deleteItemInLocalComments(commentId);
    } catch (err) {
      console.error(err);
    }
  };

  const navigateToProfile = (userId: number) => () => {
    navigate(`/user/${userId}`);
  };

  if (isLoadingNovelDetail) {
    return <div>Loading...</div>;
  }

  if (errorLoadingNovelDetail) {
    return <div>Error: {errorLoadingNovelDetail.message}</div>;
  }

  if (!novel) {
    return <div>Không tìm thấy dữ liệu truyện.</div>;
  }

  return (
    <>
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
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
              <img src={novel.poster.avatar ?? avatar} alt="Avatar" />
            </div>
            <div className={styles["poster-name"]}>
              <span onClick={navigateToProfile(novel.poster.id)}>
                {novel.poster.displayName}
              </span>
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
              isLoadingComments={isNovelCommentsLoading || isCreating}
              onPageChange={fetchNovelComments}
              onSubmit={handleNovelCommentSubmit}
              onDeleteComment={deleteComment}
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
