import { useState } from "react";
import styles from "./CommentSection.module.css";
import { formatTime } from "../../utils/formatTime";

const CommentSection = () => {
  const [commentText, setCommentText] = useState("");
  const [comments, setComments] = useState([
    {
      id: 1,
      avatar:
        "https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg",
      name: "Người dùng 1",
      text: "Bình luận đầu tiên Bình luận đầu tiên Bình luận đầu tiên...",
      time: "2020-01-15T12:00:00Z",
    },
    {
      id: 2,
      avatar:
        "https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg",
      name: "Người dùng 2",
      text: "Bình luận thứ hai",
      time: "2025-01-15T12:00:00Z",
    },
  ]);

  const handleCommentSubmit = () => {
    if (commentText.trim()) {
      const newComment = {
        id: comments.length + 1,
        avatar: "https://via.placeholder.com/40",
        name: `Người dùng ${comments.length + 1}`,
        text: commentText,
        time: "Vừa xong",
      };
      setComments([...comments, newComment]);
      setCommentText("");
    }
  };

  return (
    <div className={styles["comment-section"]}>
      <div className={styles["comment-count"]}>
        Bình luận ({comments.length})
      </div>

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

      <div className={styles["comment-list"]}>
        {comments.map((comment) => (
          <div key={comment.id} className={styles["comment-item"]}>
            <img
              src={comment.avatar}
              alt="avatar"
              className={styles["comment-avatar"]}
            />
            <div className={styles["comment-text-container"]}>
              <strong className={styles["comment-name"]}>{comment.name}</strong>
              <div className={styles["comment-text"]}>{comment.text}</div>
              <div className={styles["comment-time"]}>
                {formatTime(comment.time)}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className={styles["pagination"]}>
        <button className={styles["pagination-button"]}>Trước</button>
        <button className={styles["pagination-button"]}>Sau</button>
      </div>
    </div>
  );
};

export default CommentSection;
