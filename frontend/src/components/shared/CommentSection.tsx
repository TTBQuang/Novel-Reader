import { useState } from "react";
import styles from "./CommentSection.module.css";
import { timeAgo } from "../../utils/dateUtils";
import { useAuth } from "../../hooks/useAuth";
import avatar from "../../assets/avatar.jpg";
import { Comment } from "../../models/Comment";
import { FaEllipsisV, FaSpinner } from "react-icons/fa";

interface CommentSectionProps {
  comments: Comment[];
  totalComments: number;
  currentPage: number;
  totalPages: number;
  isLoadingComments: boolean;
  onPageChange: (page: number) => void;
  onSubmit: (commentText: string) => void;
  onDeleteComment: (commentId: number) => Promise<void>;
}

const CommentSection = ({
  comments,
  totalComments,
  currentPage,
  totalPages,
  isLoadingComments,
  onPageChange,
  onSubmit,
  onDeleteComment,
}: CommentSectionProps) => {
  const [commentText, setCommentText] = useState("");
  const { user } = useAuth();
  const [openPopupId, setOpenPopupId] = useState<number | null>(null);
  const [deletingCommentId, setDeletingCommentId] = useState<number | null>(
    null
  );
  const handleCommentSubmit = () => {
    if (commentText.trim() && onSubmit) {
      onSubmit(commentText);
      setCommentText("");
    }
  };

  const handleDeleteComment = async (commentId: number) => {
    setDeletingCommentId(commentId);
    try {
      await onDeleteComment(commentId);
      setOpenPopupId(null);
    } finally {
      setDeletingCommentId(null);
    }
  };

  const handleIconClick = (id: number) => {
    setOpenPopupId(openPopupId === id ? null : id);
  };

  if (isLoadingComments) {
    return <div>Đang tải bình luận...</div>;
  }

  return (
    <div className={styles["comment-section"]}>
      <div className={styles["comment-count"]}>Bình luận ({totalComments})</div>

      {user !== null &&
        (user.commentBlocked ? (
          <div className={styles["comment-blocked-message"]}>
            Bạn đã bị cấm bình luận.
          </div>
        ) : (
          <div className={styles["comment-input-container"]}>
            <textarea
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              placeholder="Nhập bình luận"
              className={styles["comment-textfield"]}
            />
            <div className={styles["comment-submit-container"]}>
              <button
                onClick={handleCommentSubmit}
                className={styles["comment-submit-button"]}
              >
                Đăng bình luận
              </button>
            </div>
          </div>
        ))}

      <div className={styles["comment-list"]}>
        {comments.map((comment) => (
          <div key={comment.id} className={styles["comment-item"]}>
            <img
              src={avatar}
              alt="avatar"
              className={styles["comment-avatar"]}
            />
            <div className={styles["comment-text-container"]}>
              <strong className={styles["comment-name"]}>
                {comment.user.username}
              </strong>
              <div className={styles["comment-text"]}>{comment.content}</div>
              <div className={styles["comment-time"]}>
                {timeAgo(comment.createdAt)}
              </div>
              {(user?.id === comment.user.id || user?.admin === true) && (
                <div className={styles["action-container"]}>
                  {deletingCommentId === comment.id ? (
                    <FaSpinner className={styles["loading-icon"]} />
                  ) : (
                    <>
                      <FaEllipsisV
                        className={styles["action-icon"]}
                        onClick={() => handleIconClick(comment.id)}
                      />
                      {openPopupId === comment.id && (
                        <div className={styles["popup-container"]}>
                          <div
                            className={styles["popup-item"]}
                            onClick={() => handleDeleteComment(comment.id)}
                          >
                            {"Xóa bình luận"}
                          </div>
                        </div>
                      )}
                    </>
                  )}
                </div>
              )}
            </div>
          </div>
        ))}
      </div>

      {totalPages > 1 && (
        <div className={styles["pagination"]}>
          <button
            className={styles["pagination-button"]}
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 0}
          >
            Trước
          </button>
          <button
            className={styles["pagination-button"]}
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages - 1}
          >
            Sau
          </button>
        </div>
      )}
    </div>
  );
};

export default CommentSection;
