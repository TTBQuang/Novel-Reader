import styles from "./ChapterDetailPage.module.css";
import CommentSection from "../../components/shared/CommentSection";
import ChapterDetailSidebar from "../../components/chapter-detail/ChapterDetailSidebar";

const ChapterDetailPage = () => {
  return (
    <div className={styles["chapter-detail-container"]}>
      <div className={styles["chapter-detail-content"]}>
        <div className={styles["title"]}>
          Vol 01: Cô gái đảo nghịch từ chối lòng thương xót của Chúa
        </div>
        <div className={styles["sub-title"]}>
          Chương 8: Cô gái đảo nghịch tỏa sáng nơi bóng tối ngự trị
        </div>
        <div className={styles["info"]}>
          12 Bình luận - Độ dài: 6,108 từ - Cập nhật: 5 năm
        </div>
        <div className={styles["content"]}>
          <div
            className={styles["html-content"]}
            dangerouslySetInnerHTML={{
              __html: `
              <p>Đây là đoạn văn đầu tiên của chương, với nhiều chi tiết hấp dẫn.Đây là đoạn văn đầu tiên của chương, với nhiều chi tiết hấp dẫn.Đây là đoạn văn đầu tiên của chương, với nhiều chi tiết hấp dẫn</p>
              <p>Đây là đoạn văn thứ hai, tiếp nối với các sự kiện trước đó.</p>
              <img src="https://i.hako.vn/ln/chapters/illusts/155763/cecce5cc-7aa0-498d-a5ed-3887af100a68.jpg" alt="Image 1" />
              <p>Tiếp theo là một đoạn văn nữa, miêu tả chi tiết hơn.</p>
              <img src="image2.jpg" alt="Image 2" />
            `,
            }}
          ></div>
          <div className={styles["comment-section"]}>
            <CommentSection />
          </div>
        </div>

        <ChapterDetailSidebar />
      </div>
    </div>
  );
};

export default ChapterDetailPage;
