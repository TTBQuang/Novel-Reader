import React, { useCallback } from "react";
import styles from "./PaginationComponent.module.css";

interface PaginationProps {
  currentPage: number;
  currentPageInput: number;
  setCurrentPageInput: (page: number) => void;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const PaginationComponent = ({
  currentPage,
  currentPageInput,
  setCurrentPageInput,
  totalPages,
  onPageChange,
}: PaginationProps) => {
  const validatePageNumber = useCallback(
    (page: number): number => {
      if (page < 1) return 1;
      if (page > totalPages) return totalPages;
      return page;
    },
    [totalPages]
  );

  const handlePageInputChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.value;
      if (/^\d*$/.test(value)) {
        setCurrentPageInput(parseInt(value) || 0);
      }
    },
    [setCurrentPageInput]
  );

  const handleSubmitInput = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.key === "Enter") {
        const validatedPage = validatePageNumber(currentPageInput);
        onPageChange(validatedPage);
      }
    },
    [currentPageInput, onPageChange, validatePageNumber]
  );

  const handlePageChange = useCallback(
    (newPage: number) => {
      const validatedPage = validatePageNumber(newPage);
      if (validatedPage === currentPageInput) return;

      setCurrentPageInput(validatedPage);
      onPageChange(validatedPage);
    },
    [currentPageInput, onPageChange, setCurrentPageInput, validatePageNumber]
  );

  return (
    <div className={styles.pagination}>
      <button
        className={styles["pagination-button"]}
        disabled={currentPage === 1}
        onClick={() => handlePageChange(currentPageInput - 1)}
      >
        Trước
      </button>

      <input
        type="number"
        value={currentPageInput}
        onChange={handlePageInputChange}
        onKeyDown={handleSubmitInput}
        className={styles["page-input"]}
      />
      <span className={styles["page-total"]}>/{totalPages}</span>

      <button
        className={styles["pagination-button"]}
        disabled={currentPage === totalPages}
        onClick={() => handlePageChange(currentPageInput + 1)}
      >
        Sau
      </button>
    </div>
  );
};

export default PaginationComponent;
