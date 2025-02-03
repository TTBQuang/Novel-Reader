import { useState } from "react";
import TopAppBar from "../../components/shared/TopAppBar";
import NovelSpinner from "../../components/home/NovelSpinner";
import MainBanner from "../../components/shared/MainBanner";
import NovelItemComponent from "../../components/home/NovelItemComponent";
import { NovelItem } from "../../models/NovelItem";
import StatusFilter from "../../components/home/StatusFilter";
import GenreFilter from "../../components/home/GenreFIlter";
import PaginationComponent from "../../components/home/PaginationComponent";
import styles from "./HomePage.module.css";

const novelData: NovelItem = {
  id: 1,
  name: "One Piece One PieceOne PieceOne PieceOne PieceOne PieceOne PieceOne Piece",
  image:
    "https://upload.wikimedia.org/wikipedia/vi/9/90/One_Piece%2C_Volume_61_Cover_%28Japanese%29.jpg",
  latestChapter: "Chapter 1095 Chapter 1095 Chapter 1095",
};
const novelsList = Array(42).fill(novelData);
const genres = [
  { id: 1, name: "Action" },
  { id: 2, name: "Adapted to Anime" },
  { id: 3, name: "Adapted to Drama CD" },
  { id: 4, name: "Adapted to Manga" },
  { id: 5, name: "Adult" },
  { id: 6, name: "Adventure" },
  { id: 7, name: "Age Gap" },
  { id: 8, name: "Boys Love" },
  { id: 9, name: "Character Growth" },
  { id: 10, name: "Chinese Novel" },
  { id: 11, name: "Comedy" },
  { id: 12, name: "Cooking" },
  { id: 13, name: "Different Social Status" },
  { id: 14, name: "Drama" },
  { id: 15, name: "Ecchi" },
  { id: 16, name: "English Novel" },
  { id: 17, name: "Fantasy" },
  { id: 18, name: "Female Protagonist" },
  { id: 19, name: "Game" },
  { id: 20, name: "Gender Bender" },
  { id: 21, name: "Harem" },
  { id: 22, name: "Historical" },
  { id: 23, name: "Horror" },
  { id: 24, name: "Incest" },
  { id: 25, name: "Isekai" },
  { id: 26, name: "Josei" },
  { id: 27, name: "Korean Novel" },
  { id: 28, name: "Magic" },
  { id: 29, name: "Martial Arts" },
  { id: 30, name: "Mature" },
  { id: 31, name: "Mecha" },
  { id: 32, name: "Military" },
  { id: 33, name: "Misunderstanding" },
  { id: 34, name: "Mystery" },
  { id: 35, name: "Netorare" },
  { id: 36, name: "One shot" },
  { id: 37, name: "Otome Game" },
  { id: 38, name: "Parody" },
  { id: 39, name: "Psychological" },
  { id: 40, name: "Reverse Harem" },
  { id: 41, name: "Romance" },
  { id: 42, name: "School Life" },
  { id: 43, name: "Science Fiction" },
  { id: 44, name: "Seinen" },
  { id: 45, name: "Shoujo" },
  { id: 46, name: "Shoujo ai" },
  { id: 47, name: "Shounen" },
  { id: 48, name: "Shounen ai" },
  { id: 49, name: "Slice of Life" },
  { id: 50, name: "Slow Life" },
  { id: 51, name: "Sports" },
  { id: 52, name: "Super Power" },
  { id: 53, name: "Supernatural" },
  { id: 54, name: "Suspense" },
  { id: 55, name: "Tragedy" },
  { id: 56, name: "Wars" },
  { id: 57, name: "Web Novel" },
  { id: 58, name: "Workplace" },
  { id: 59, name: "Yuri" },
];

const HomePage = () => {
  const [currentPage, setCurrentPage] = useState<number>(1);
  const totalPages = 10;

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  return (
    <>
      <TopAppBar />
      <MainBanner />
      <div className={styles["home-page"]}>
        <div className={styles["home-page-content"]}>
          <div className={styles["left-content"]}>
            <NovelSpinner />
            <div className={styles["novels-list"]}>
              {novelsList.map((novel, index) => (
                <NovelItemComponent key={index} novelItem={novel} />
              ))}
            </div>
            <div className={styles["pagination-container"]}>
              <PaginationComponent
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </div>
          </div>
          <div className={styles["right-content"]}>
            <StatusFilter />
            <GenreFilter genres={genres} />
          </div>
        </div>
      </div>
    </>
  );
};

export default HomePage;
