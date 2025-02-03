import { useState } from "react";
import { NovelStatus } from "../../models/NovelStatus";
import styles from "./StatusFilter.module.css";

const StatusFilter = () => {
  const [selectedStatus, setSelectedStatus] = useState<NovelStatus[]>([]);

  const handleCheckboxChange = (status: NovelStatus) => {
    setSelectedStatus((prev) =>
      prev.includes(status)
        ? prev.filter((item) => item !== status)
        : [...prev, status]
    );
  };

  const handleApplyClick = () => {
    console.log("Áp dụng filter với các tình trạng:", selectedStatus);
  };

  return (
    <div className={styles["status-filter-container"]}>
      <div className={styles["status-filter-header"]}>
        <strong>Tình trạng</strong>
      </div>
      <div className={styles["status-checkboxes"]}>
        {Object.values(NovelStatus).map((status) => (
          <div key={status}>
            <input
              type="checkbox"
              id={status}
              checked={selectedStatus.includes(status)}
              onChange={() => handleCheckboxChange(status)}
            />
            <label htmlFor={status}>{status}</label>
          </div>
        ))}
      </div>
      <button className={styles["apply-button"]} onClick={handleApplyClick}>
        Áp dụng
      </button>
    </div>
  );
};

export default StatusFilter;
