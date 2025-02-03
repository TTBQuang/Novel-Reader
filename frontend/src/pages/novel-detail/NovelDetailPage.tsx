import TopAppBar from "../../components/shared/TopAppBar";
import MainBanner from "../../components/shared/MainBanner";
import NovelDetail from "../../components/novel-detail/NovelDetail";
import ChapterGroupComponent from "../../components/novel-detail/ChapterGroupComponent";
import CommentSection from "../../components/shared/CommentSection";
import styles from "./NovelDetailPage.module.css";

const NovelDetailPage = () => {
  const fakeNovelData = {
    title: "Re:Zero - Bắt Đầu Lại Ở Thế Giới Khác",
    coverImage:
      "https://noithatbinhminh.com.vn/wp-content/uploads/2022/08/anh-dep-44.jpg.webp",
    genres: ["Fantasy", "Adventure", "Drama", "Romance", "Psychological"],
    author: "Tappei Nagatsuki",
    artist: "Shinichirou Otsuka",
    status: "Đang tiến hành",
    summary:
      "Subaru Natsuki là một học sinh trung học bình thường, một hôm đột nhiên bị triệu hồi đến một thế giới fantasy khác. Khi vừa đến nơi, cậu bị một nhóm côn đồ tấn công và được một cô gái xinh đẹp tóc bạc tên Emilia cứu giúp. Để đền ơn cô, Subaru quyết định giúp cô tìm lại huy hiệu bị đánh cắp, nhưng mọi chuyện không hề đơn giản khi cả hai bị cuốn vào một chuỗi sự kiện đẫm máu. Subaru phát hiện ra mình có khả năng 'Trở Về Từ Cái Chết', cho phép cậu quay trở lại thời điểm nhất định sau khi chết. Với năng lực này, cậu thề sẽ cứu được Emilia và những người bạn khỏi số phận bi thảm.",
  };

  const fakeChapters = Array.from({ length: 30 }, (_, index) => ({
    title: `Chapter ${index + 1}: Khởi đầu`,
    date: "01/01/2023",
  }));

  return (
    <>
      <TopAppBar />
      <MainBanner />
      <div className={styles["page-content-container"]}>
        <div className={styles["novel-detail-container"]}>
          <div className={styles["novel-detail"]}>
            <NovelDetail {...fakeNovelData} />
          </div>
          <div className={styles["avatar-row"]}>
            <div className={styles["avatar"]}>
              <img
                src="https://noithatbinhminh.com.vn/wp-content/uploads/2022/08/anh-dep-44.jpg.webp"
                alt="Avatar"
              />
            </div>
            <div className={styles["poster-name"]}>
              <span>Tran Ton Buu Quang</span>
            </div>
          </div>

          <div className={styles["chapter-group-container"]}>
            <ChapterGroupComponent
              groupTitle="Danh sách chương mới nhất"
              imageSrc="https://noithatbinhminh.com.vn/wp-content/uploads/2022/08/anh-dep-44.jpg.webp"
              chapters={fakeChapters}
            />
          </div>

          <div className={styles["chapter-group-container"]}>
            <ChapterGroupComponent
              groupTitle="Danh sách chương mới nhất"
              imageSrc="https://noithatbinhminh.com.vn/wp-content/uploads/2022/08/anh-dep-44.jpg.webp"
              chapters={fakeChapters}
            />
          </div>

          <CommentSection />
        </div>
      </div>
    </>
  );
};

export default NovelDetailPage;
