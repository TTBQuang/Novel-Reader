import { useState } from "react";
import { GenreListProps } from "../../models/Genre";
import styles from "./GenreFilter.module.css";

const GenreFilter = ({ genres }: GenreListProps) => {
  const [selectedGenres, setSelectedGenres] = useState<number[]>([]);

  const handleCheckboxChange = (genreId: number) => {
    setSelectedGenres((prev) =>
      prev.includes(genreId)
        ? prev.filter((id) => id !== genreId)
        : [...prev, genreId]
    );
  };

  const handleApplyClick = () => {
    console.log("Áp dụng filter với các thể loại:", selectedGenres);
  };

  return (
    <div className={styles["genre-filter-container"]}>
      <div className={styles["genre-filter-header"]}>
        <strong>Thể loại</strong>
      </div>
      <div className={styles["genre-checkboxes"]}>
        {genres.map((genre) => (
          <div key={genre.id}>
            <input
              type="checkbox"
              id={`genre-${genre.id}`}
              checked={selectedGenres.includes(genre.id)}
              onChange={() => handleCheckboxChange(genre.id)}
            />
            <label htmlFor={`genre-${genre.id}`}>{genre.name}</label>
          </div>
        ))}
      </div>
      <button className={styles["apply-button"]} onClick={handleApplyClick}>
        Áp dụng
      </button>
    </div>
  );
};

export default GenreFilter;
