import React, { useState, useEffect } from "react";
import styles from "./UserManagementPage.module.css";
import { FaEllipsisV } from "react-icons/fa";
import TopAppBar from "../../components/shared/TopAppBar";
import { useNavigate } from "react-router-dom";
import { useUsers } from "../../hooks/useUsers";
import { useCommentBlocked } from "../../hooks/useCommentBlocked";
import { User } from "../../models/User";
import { USERS_PER_PAGE } from "../../utils/constants";

const UserManagementPage = () => {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState(search);
  const [currentPage, setCurrentPage] = useState(0);
  const [openPopupId, setOpenPopupId] = useState<number | null>(null);
  const { updateStatus } = useCommentBlocked();

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 200);
    return () => clearTimeout(timer);
  }, [search]);

  const { usersResponse, loading, error } = useUsers(
    currentPage,
    USERS_PER_PAGE,
    debouncedSearch
  );

  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "auto" });
  }, [currentPage]);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearch(e.target.value);
    setCurrentPage(0);
  };

  const handlePrevPage = () => {
    setCurrentPage((prev) => Math.max(prev - 1, 0));
  };

  const handleNextPage = () => {
    if (usersResponse) {
      setCurrentPage((prev) =>
        Math.min(prev + 1, usersResponse.page.totalPages - 1)
      );
    }
  };

  const handleIconClick = (id: number) => {
    setOpenPopupId((prev) => (prev === id ? null : id));
  };

  const handleBanComments = async (user: User) => {
    try {
      await updateStatus(user.id, user.commentBlocked);

      user.commentBlocked = !user.commentBlocked;
      setOpenPopupId(null);
    } catch (err: unknown) {
      if (err instanceof Error) {
        alert(err.message);
      } else {
        alert("Error updating comment status");
      }
    }
  };

  return (
    <div className={styles.pageWrapper}>
      <TopAppBar onSearch={() => {}} onLogoClick={() => navigate("/")} />
      <div className={styles.container}>
        <div className={styles.searchBarContainer}>
          <input
            type="text"
            placeholder="Tìm kiếm..."
            value={search}
            onChange={handleSearchChange}
            className={styles.searchInput}
          />
        </div>

        <div className={styles.userHeader}>
          <span className={styles.userId}>ID</span>
          <span className={styles.username}>Username</span>
          <span className={styles.email}>Email</span>
        </div>

        {loading && <div>Loading...</div>}
        {error && <div>Error: {error}</div>}
        {usersResponse && (
          <div className={styles.userList}>
            {usersResponse.content.map((user) => (
              <div key={user.id} className={styles.userItem}>
                <div className={styles.userInfo}>
                  <span className={styles.userId}>#{user.id}</span>
                  <span className={styles.username}>{user.username}</span>
                  <span className={styles.email}>{user.email}</span>
                </div>
                <div className={styles.actionContainer}>
                  <FaEllipsisV
                    className={styles.actionIcon}
                    onClick={() => handleIconClick(user.id)}
                  />
                  {openPopupId === user.id && (
                    <div className={styles.popupContainer}>
                      <div
                        className={styles.popupItem}
                        onClick={() => handleBanComments(user)}
                      >
                        {user.commentBlocked
                          ? "Cho phép bình luận"
                          : "Cấm bình luận"}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {usersResponse && (
          <div className={styles.pagination}>
            <button
              onClick={handlePrevPage}
              disabled={currentPage === 0}
              className={styles.pageButton}
            >
              Trước
            </button>
            <span className={styles["page-info"]}>
              Trang {currentPage + 1} / {usersResponse.page.totalPages}
            </span>
            <button
              onClick={handleNextPage}
              disabled={currentPage === usersResponse.page.totalPages - 1}
              className={styles.pageButton}
            >
              Sau
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserManagementPage;
