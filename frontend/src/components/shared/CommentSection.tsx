// CommentSection.tsx
import { useState } from "react";
import styles from "./CommentSection.module.css";
import { timeAgo } from "../../utils/dateUtils";
import { useAuth } from "../../hooks/useAuth";
import avatar from "../../assets/avatar.jpg";
import { Comment } from "../../models/Comment";

interface CommentSectionProps {
  comments: Comment[];
  totalComments: number;
  currentPage: number;
  totalPages: number;
  isLoading: boolean;
  onPageChange: (page: number) => void;
  onSubmit?: (commentText: string) => void;
}

const CommentSection = ({
  comments,
  totalComments,
  currentPage,
  totalPages,
  isLoading,
  onPageChange,
  onSubmit,
}: CommentSectionProps) => {
  const [commentText, setCommentText] = useState("");
  const { user } = useAuth();

  const handleCommentSubmit = () => {
    if (commentText.trim() && onSubmit) {
      onSubmit(commentText);
      setCommentText("");
    }
  };

  if (isLoading) {
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
