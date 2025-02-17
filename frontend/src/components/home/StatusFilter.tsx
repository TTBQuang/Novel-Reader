import { useState, useCallback, memo } from "react";
import { NovelStatus } from "../../models/NovelStatus";
import styles from "./StatusFilter.module.css";

interface StatusFilterProps {
  onApply: (selectedStatus: NovelStatus[]) => void;
}

const StatusFilter = memo(({ onApply }: StatusFilterProps) => {
  const [selectedStatus, setSelectedStatus] = useState<Set<NovelStatus>>(
    new Set()
  );

  const handleCheckboxChange = useCallback((status: NovelStatus) => {
    setSelectedStatus((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(status)) {
        newSet.delete(status);
      } else {
        newSet.add(status);
      }
      return newSet;
    });
  }, []);

  const handleApplyClick = useCallback(() => {
    onApply(Array.from(selectedStatus));
  }, [selectedStatus, onApply]);

  return (
    <div className={styles["status-filter-container"]}>
      <div className={styles["status-filter-header"]}>
        <strong>Tình trạng</strong>
      </div>
      <div className={styles["status-checkboxes"]}>
        {Object.values(NovelStatus).map((status) => (
          <div key={status} className={styles["checkbox-item"]}>
            <input
              type="checkbox"
              id={status}
              checked={selectedStatus.has(status)}
              onChange={() => handleCheckboxChange(status)}
              className={styles["checkbox-input"]}
            />
            <label htmlFor={status} className={styles["checkbox-label"]}>
              {status}
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

export default StatusFilter;
