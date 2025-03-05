import { toast, ToastContainer } from "react-toastify";
import TopAppBar from "../../components/shared/TopAppBar";
import styles from "./UserDetailPage.module.css";
import { useNavigate, useParams } from "react-router-dom";
import { useContext, useRef, useState } from "react";
import avatarImage from "../../assets/avatar.jpg";
import { useUserDetail } from "../../hooks/useUserDetail";
import { UserContext } from "../../context/UserContext";
import { timeAgo } from "../../utils/dateUtils";
import { mapApiStatusToNovelStatus } from "../../models/NovelStatus";
import { MdModeEdit, MdCheck, MdClose } from "react-icons/md";
import { useUpdateDisplayName } from "../../hooks/useUpdateDisplayName";
import { User } from "../../models/User";
import { useUpdateAvatar } from "../../hooks/useUpdateAvatar";
import { FaSpinner } from "react-icons/fa";
import { useUpdateCoverImage } from "../../hooks/useUpdateCoverImage";

const UserDetailPage: React.FC = () => {
  const navigate = useNavigate();
  const { userId } = useParams<{ userId: string }>();
  const {
    user: profileUser,
    loading: loadingUserDetail,
    setUser: setUserDetail,
    error: errorLoadingUserDetail,
  } = useUserDetail(Number(userId));
  const { user: currentUser, setUser: setUserContext } =
    useContext(UserContext) ?? {};
  const isOwner = currentUser?.id === Number(userId);

  const profileImageInputRef = useRef<HTMLInputElement | null>(null);
  const coverImageInputRef = useRef<HTMLInputElement | null>(null);
  const [isEditingName, setIsEditingName] = useState(false);
  const [editedName, setEditedName] = useState("");
  const {
    updateName,
    error: errorUpdatingDisplayName,
    success,
  } = useUpdateDisplayName();

  const {
    uploadAndUpdateAvatar,
    loading: loadingUpdateAvatar,
    error: errorUpdateAvatar,
  } = useUpdateAvatar();

  const {
    uploadAndUpdateCoverImage,
    loading: loadingUpdateCoverImage,
    error: errorUpdateCoverImage,
  } = useUpdateCoverImage();

  const handleAvatarChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const imageUrl = URL.createObjectURL(file);
      await uploadAndUpdateAvatar(imageUrl, Number(userId));

      if (errorUpdateAvatar) {
        toast.error(errorUpdateAvatar);
      } else {
        if (profileUser) {
          const updatedUser: User = { ...profileUser, avatar: imageUrl };
          setUserDetail(updatedUser);
          setUserContext?.(updatedUser);
        }
      }
    }
  };

  const handleCoverImageChange = async (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const file = e.target.files?.[0];
    if (file) {
      const imageUrl = URL.createObjectURL(file);
      await uploadAndUpdateCoverImage(imageUrl, Number(userId));

      if (errorUpdateCoverImage) {
        toast.error(errorUpdateCoverImage);
      } else {
        if (profileUser) {
          const updatedUser: User = { ...profileUser, coverImage: imageUrl };
          setUserDetail(updatedUser);
          setUserContext?.(updatedUser);
        }
      }
    }
  };

  const startEditDisplayName = () => {
    if (isOwner) {
      setEditedName(profileUser?.displayName || "");
      setIsEditingName(true);
    }
  };

  const saveDisplayName = async () => {
    if (!editedName.trim()) return;

    try {
      await updateName(editedName);

      if (success) {
        if (profileUser) {
          const updatedUser: User = { ...profileUser, displayName: editedName };
          setUserDetail(updatedUser);
          setUserContext?.(updatedUser);
        }

        setIsEditingName(false);
      } else {
        toast.error(errorUpdatingDisplayName);
      }
    } catch {
      toast.error("Failed to update display name");
    }
  };

  const cancelEditDisplayName = () => {
    setIsEditingName(false);
  };

  const navigateToNovelDetailPage = (novelId: number) => () => {
    navigate(`/novel/${novelId}`);
  };

  if (loadingUserDetail) return <div>Loading...</div>;
  if (errorLoadingUserDetail) return <div>Error: {errorLoadingUserDetail}</div>;

  return (
    <>
      <TopAppBar onSearch={() => {}} onLogoClick={() => navigate("/")} />
      <div className={styles["page-container"]}>
        <ToastContainer
          position="top-right"
          autoClose={3000}
          hideProgressBar
          theme="light"
        />
        <div className={styles["user-profile-container"]}>
          <div
            className={`${styles["cover-image"]} ${
              isOwner ? styles["hover-effect"] : ""
            }`}
            onClick={
              isOwner && !loadingUpdateCoverImage
                ? () => coverImageInputRef.current?.click()
                : undefined
            }
            style={
              profileUser?.coverImage
                ? {
                    backgroundImage: `url("${profileUser.coverImage}")`,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                    backgroundRepeat: "no-repeat",
                    opacity: loadingUpdateCoverImage ? 0.5 : 1,
                    position: "relative",
                  }
                : { opacity: loadingUpdateCoverImage ? 0.5 : 1 }
            }
          >
            {loadingUpdateCoverImage && (
              <div className={styles["loading-overlay"]}>
                <FaSpinner className={styles["spinner"]} />
              </div>
            )}
          </div>

          <div className={styles["user-display-name"]}>
            <div
              className={`${styles["avatar-container"]} ${
                isOwner ? styles["hover-effect"] : ""
              }`}
              onClick={
                isOwner && !loadingUpdateAvatar
                  ? () => profileImageInputRef.current?.click()
                  : undefined
              }
              style={{ position: "relative" }}
            >
              {loadingUpdateAvatar ? (
                <div className={styles["avatar-loading-overlay"]}>
                  <FaSpinner className={styles["spinner"]} />
                </div>
              ) : (
                <img
                  src={profileUser?.avatar || avatarImage}
                  alt="User avatar"
                  className={styles["avatar"]}
                />
              )}
            </div>

            <div className={styles["user-info"]}>
              <h2 className={styles["display-name"]}>
                {isEditingName ? (
                  <>
                    <input
                      type="text"
                      value={editedName}
                      onChange={(e) => setEditedName(e.target.value)}
                      className={styles["display-name-input"]}
                      autoFocus
                    />
                    <MdCheck
                      className={styles["edit-icon"]}
                      onClick={saveDisplayName}
                    />
                    <MdClose
                      className={styles["edit-icon"]}
                      onClick={cancelEditDisplayName}
                    />
                  </>
                ) : (
                  <>
                    {profileUser?.displayName || "Unknown User"}
                    {isOwner && (
                      <MdModeEdit
                        className={styles["edit-icon"]}
                        onClick={startEditDisplayName}
                      />
                    )}
                  </>
                )}
              </h2>
              <h2 className={styles["user-id"]}>#{profileUser?.id}</h2>
            </div>
          </div>

          <input
            type="file"
            accept="image/*"
            style={{ display: "none" }}
            ref={profileImageInputRef}
            onChange={handleAvatarChange}
          />
          <input
            type="file"
            accept="image/*"
            style={{ display: "none" }}
            ref={coverImageInputRef}
            onChange={handleCoverImageChange}
          />
        </div>

        <div className={styles["stats-and-novels-wrapper"]}>
          <div className={styles["stats-container"]}>
            <div className={styles["stats-list"]}>
              <div className={styles["stat-item"]}>
                <div className={styles["stat-number"]}>
                  {profileUser?.chaptersCount ?? 0}
                </div>
                <div className={styles["stat-label"]}>Chương đã đăng</div>
              </div>
              <div className={styles["stat-item"]}>
                <div className={styles["stat-number"]}>
                  {profileUser?.commentsCount ?? 0}
                </div>
                <div className={styles["stat-label"]}>Bình luận</div>
              </div>
            </div>
            <div className={styles["stat-divider"]}></div>
            <div className={styles["join-date"]}>
              Tham gia:{" "}
              {profileUser?.createdAt
                ? new Date(profileUser.createdAt).toLocaleDateString("vi-VN")
                : "N/A"}
            </div>
          </div>

          <div className={styles["novels-container"]}>
            <div className={styles["novels-header"]}>
              <div className={styles["novels-count"]}>
                {profileUser?.ownNovels.length ?? 0}
              </div>
              <div className={styles["novels-title"]}>Truyện đã đăng</div>
            </div>
            <div className={styles["novels-divider"]}></div>

            <div className={styles["novel-list"]}>
              {profileUser?.ownNovels.map((novel) => (
                <div key={novel.id} className={styles["novel-item"]}>
                  <img
                    src={novel.cover}
                    alt={`Novel Cover`}
                    className={styles["novel-image"]}
                  />
                  <div className={styles["novel-info"]}>
                    <div
                      className={styles["novel-title"]}
                      onClick={navigateToNovelDetailPage(novel.id)}
                    >
                      {novel.name}
                    </div>
                    <div className={styles["novel-meta-container"]}>
                      <p>
                        <span className={styles["meta-label"]}>
                          Tình trạng:
                        </span>{" "}
                        <span className={styles["meta-value"]}>
                          {mapApiStatusToNovelStatus(novel.status)}
                        </span>
                      </p>
                      <p>
                        <span className={styles["meta-label"]}>Lần cuối:</span>{" "}
                        <span className={styles["meta-value"]}>
                          {timeAgo(novel.lastUpdateDate)}
                        </span>
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default UserDetailPage;
