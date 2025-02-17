import { useState, useCallback, memo } from "react";
import { Genre } from "../../models/Genre";
import styles from "./GenreFilter.module.css";

interface GenreFilterProps {
  onApply: (selectedGenreIds: number[]) => void;
  genres: Genre[];
}

const GenreFilter = memo(({ genres, onApply }: GenreFilterProps) => {
  const [selectedGenres, setSelectedGenres] = useState<Set<number>>(new Set());

  const handleCheckboxChange = useCallback((genreId: number) => {
    setSelectedGenres((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(genreId)) {
        newSet.delete(genreId);
      } else {
        newSet.add(genreId);
      }
      return newSet;
    });
  }, []);

  const handleApplyClick = useCallback(() => {
    onApply(Array.from(selectedGenres));
  }, [selectedGenres, onApply]);

  return (
    <div className={styles["genre-filter-container"]}>
      <div className={styles["genre-filter-header"]}>
        <strong>Thể loại</strong>
      </div>
      <div className={styles["genre-checkboxes"]}>
        {genres.map((genre) => (
          <div key={genre.id} className={styles["checkbox-item"]}>
            <input
              type="checkbox"
              id={`genre-${genre.id}`}
              checked={selectedGenres.has(genre.id)}
              onChange={() => handleCheckboxChange(genre.id)}
              className={styles["checkbox-input"]}
            />
            <label
              htmlFor={`genre-${genre.id}`}
              className={styles["checkbox-label"]}
            >
              {genre.name}
            </label>
          </div>
        ))}
      </div>
      <button className={styles["apply-button"]} onClick={handleApplyClick}>
        Áp dụng
      </button>
    </div>
  );
});

export default GenreFilter;
