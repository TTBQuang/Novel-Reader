import React, { useState } from "react";
import styles from "./NovelSpinner.module.css";
import { NovelSortOption } from "../../models/NovelSortOption";

const NovelSpinner = () => {
  const [selectedOption, setSelectedOption] = useState<string>("");

  const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedOption(event.target.value);
  };

  return (
    <>
      <select
        className={styles["spinner"]}
        value={selectedOption}
        onChange={handleChange}
      >
        {Object.values(NovelSortOption).map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </select>
    </>
  );
};

export default NovelSpinner;
