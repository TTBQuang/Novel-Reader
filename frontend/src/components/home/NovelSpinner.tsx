import { memo } from "react";
import { FaChevronDown } from "react-icons/fa";
import styles from "./NovelSpinner.module.css";
import { NovelSortOption } from "../../models/NovelSortOption";

interface NovelSpinnerProps {
  selectedOption: NovelSortOption;
  onSortOptionChange: (option: NovelSortOption) => void;
}

const NovelSpinner = memo(
  ({ selectedOption, onSortOptionChange }: NovelSpinnerProps) => {
    return (
      <div className={styles["spinner-container"]}>
        <select
          className={styles["spinner"]}
          value={selectedOption}
          onChange={(e) =>
            onSortOptionChange(e.target.value as NovelSortOption)
          }
        >
          {Object.values(NovelSortOption).map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
        <FaChevronDown className={styles["spinner-icon"]} />
      </div>
    );
  }
);

export default NovelSpinner;
