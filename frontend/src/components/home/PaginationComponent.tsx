import React from "react";
import styles from "./PaginationComponent.module.css";

interface PaginationProps {
  currentPageInput: number;
  setCurrentPageInput: (page: number) => void;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const PaginationComponent = ({
  currentPageInput,
  setCurrentPageInput,
  totalPages,
  onPageChange,
}: PaginationProps) => {
  const handlePageInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    if (/^\d*$/.test(value)) {
      setCurrentPageInput(parseInt(value, 10));
    }
  };

  const handleSubmitInput = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      let page;
      if (currentPageInput < 1) {
        page = 1;
      } else if (currentPageInput > totalPages) {
        page = totalPages;
      } else {
        page = Math.max(1, Math.min(totalPages, currentPageInput));
      }
      onPageChange(page);
    }
  };

  const handlePageChange = (newPage: number) => {
    if (newPage > totalPages || newPage < 1) {
      return;
    }
    const page = Math.max(1, Math.min(totalPages, newPage));
    setCurrentPageInput(page);
    onPageChange(page);
  };

  return (
    <div className={styles.pagination}>
      <button
        className={styles["pagination-button"]}
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
        onClick={() => handlePageChange(currentPageInput + 1)}
      >
        Sau
      </button>
    </div>
  );
};

export default PaginationComponent;
