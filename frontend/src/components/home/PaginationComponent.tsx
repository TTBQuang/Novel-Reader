import React, { useState } from "react";
import styles from "./PaginationComponent.module.css";

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const PaginationComponent = ({
  currentPage,
  totalPages,
  onPageChange,
}: PaginationProps) => {
  const [pageInput, setPageInput] = useState<string>(currentPage.toString());

  const handlePageInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    if (/^\d*$/.test(value)) {
      setPageInput(value);
    }
  };

  const handlePageSubmit = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      const page = Math.max(1, Math.min(totalPages, parseInt(pageInput, 10)));
      if (page >= 1 && page <= totalPages) {
        onPageChange(page);
        alert(`Bạn đã nhập số trang: ${page}`);
      } else {
        alert(`Số trang không hợp lệ!`);
      }
    }
  };

  const handlePageChange = (newPage: number) => {
    const page = Math.max(1, Math.min(totalPages, newPage));
    setPageInput(page.toString());
    onPageChange(page);
  };

  return (
    <div className={styles.pagination}>
      <button
        className={styles["pagination-button"]}
        onClick={() => handlePageChange(currentPage - 1)}
      >
        Trước
      </button>

      <input
        type="text"
        value={pageInput}
        onChange={handlePageInputChange}
        onKeyDown={handlePageSubmit}
        className={styles["page-input"]}
      />
      <span className={styles["page-total"]}>/{totalPages}</span>

      <button
        className={styles["pagination-button"]}
        onClick={() => handlePageChange(currentPage + 1)}
      >
        Sau
      </button>
    </div>
  );
};

export default PaginationComponent;
