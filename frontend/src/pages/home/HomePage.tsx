import { useSearchParams } from "react-router-dom";
import { useState, useEffect, useCallback } from "react";
import TopAppBar from "../../components/shared/TopAppBar";
import NovelSpinner from "../../components/home/NovelSpinner";
import MainBanner from "../../components/shared/MainBanner";
import NovelItemComponent from "../../components/home/NovelItemComponent";
import GenreFilter from "../../components/home/GenreFIlter";
import PaginationComponent from "../../components/home/PaginationComponent";
import styles from "./HomePage.module.css";
import { useNovels } from "../../hooks/useNovels";
import { useGenres } from "../../hooks/useGenres";
import { NOVELS_PER_PAGE } from "../../utils/constants";
import {
  NovelSortOption,
  mapNovelSortOptionToApiValue,
} from "../../models/NovelSortOption";
import {
  NovelStatus,
  mapNovelStatusToApiValue,
} from "../../models/NovelStatus";
import StatusFilter from "../../components/home/StatusFilter";
import { FaChevronDown, FaTimes } from "react-icons/fa";

interface FilterState {
  page: number;
  pageInput: number;
  sortOption: NovelSortOption;
  keyword: string;
  statusFilter: string;
  genreFilter: string;
}

const DEFAULT_FILTERS: FilterState = {
  page: 1,
  pageInput: 1,
  sortOption: NovelSortOption.AZ,
  keyword: "",
  statusFilter: "",
  genreFilter: "",
};

const HomePage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [showMobileFilters, setShowMobileFilters] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth < 992);

  const [filters, setFilters] = useState<FilterState>(() => ({
    ...DEFAULT_FILTERS,
    page: Number(searchParams.get("page")) || 1,
    pageInput: Number(searchParams.get("page")) || 1,
    keyword: searchParams.get("keyword") || "",
  }));

  const { page, pageInput, sortOption, keyword, statusFilter, genreFilter } =
    filters;

  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth < 992);
      if (window.innerWidth >= 992) {
        setShowMobileFilters(false);
      }
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const updateUrlParams = useCallback(
    (newFilters: Partial<FilterState>) => {
      const params = new URLSearchParams(searchParams);

      if (newFilters.page !== undefined) {
        params.set("page", newFilters.page.toString());
      }

      if (newFilters.keyword !== undefined) {
        if (newFilters.keyword.trim()) {
          params.set("keyword", newFilters.keyword);
        } else {
          params.delete("keyword");
        }
      }

      setSearchParams(params);
    },
    [searchParams, setSearchParams]
  );

  useEffect(() => {
    const urlPage = Number(searchParams.get("page")) || 1;
    const urlKeyword = searchParams.get("keyword") || "";

    if (urlPage !== filters.page || urlKeyword !== filters.keyword) {
      setFilters((prev) => ({
        ...prev,
        page: urlPage,
        pageInput: urlPage,
        keyword: urlKeyword,
      }));
    }
  }, [searchParams]);

  // Prevent body scroll when mobile filters are open
  useEffect(() => {
    if (showMobileFilters) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "auto";
    }

    return () => {
      document.body.style.overflow = "auto";
    };
  }, [showMobileFilters]);

  const { novelsList, loading, totalPages } = useNovels(
    page - 1,
    NOVELS_PER_PAGE,
    mapNovelSortOptionToApiValue(sortOption),
    keyword,
    statusFilter,
    genreFilter
  );

  const { genresList } = useGenres();

  const handleSort = useCallback(
    (newSortOption: NovelSortOption) => {
      setFilters((prev) => ({
        ...prev,
        sortOption: newSortOption,
        page: 1,
        pageInput: 1,
      }));
      updateUrlParams({ page: 1 });
    },
    [updateUrlParams]
  );

  const handlePageChange = useCallback(
    (newPage: number) => {
      if (newPage >= 1 && newPage <= totalPages) {
        setFilters((prev) => ({
          ...prev,
          page: newPage,
          pageInput: newPage,
        }));
        updateUrlParams({ page: newPage });
      }
    },
    [totalPages, updateUrlParams]
  );

  const handleSearch = useCallback(
    (newKeyword: string) => {
      setFilters((prev) => ({
        ...prev,
        keyword: newKeyword,
        page: 1,
        pageInput: 1,
      }));
      updateUrlParams({
        keyword: newKeyword,
        page: 1,
      });
    },
    [updateUrlParams]
  );

  const handleStatusApply = useCallback(
    (selectedStatus: NovelStatus[]) => {
      const statusParam = selectedStatus
        .map((status) => mapNovelStatusToApiValue(status))
        .join(",");

      setFilters((prev) => ({
        ...prev,
        statusFilter: statusParam,
        page: 1,
        pageInput: 1,
      }));
      updateUrlParams({ page: 1 });
      setShowMobileFilters(false);
    },
    [updateUrlParams]
  );

  const handleGenreApply = useCallback(
    (selectedGenreIds: number[]) => {
      setFilters((prev) => ({
        ...prev,
        genreFilter: selectedGenreIds.join(","),
        page: 1,
        pageInput: 1,
      }));
      updateUrlParams({ page: 1 });
      setShowMobileFilters(false);
    },
    [updateUrlParams]
  );

  const handleLogoClick = useCallback(() => {
    setFilters(DEFAULT_FILTERS);
    setSearchParams({});
  }, [setSearchParams]);

  const handlePageInputChange = useCallback((newPageInput: number) => {
    setFilters((prev) => ({ ...prev, pageInput: newPageInput }));
  }, []);

  const toggleMobileFilters = useCallback(() => {
    setShowMobileFilters((prev) => !prev);
  }, []);

  const renderNovelsList = () => {
    if (loading) {
      return <div className={styles["loading-container"]}>Loading...</div>;
    }

    return (
      <div className={styles["novels-list"]}>
        {novelsList.map((novel, index) => (
          <NovelItemComponent key={novel.id || index} novelItem={novel} />
        ))}
      </div>
    );
  };

  const renderFilters = () => (
    <>
      <StatusFilter onApply={handleStatusApply} />
      <GenreFilter genres={genresList} onApply={handleGenreApply} />
    </>
  );

  return (
    <>
      <TopAppBar onSearch={handleSearch} onLogoClick={handleLogoClick} />
      <MainBanner />
      <div className={styles["home-page"]}>
        <div className={styles["home-page-content"]}>
          <div className={styles["left-content"]}>
            {isMobile && (
              <div
                className={styles["filter-toggle"]}
                onClick={toggleMobileFilters}
              >
                <span className={styles["filter-toggle-text"]}>Filters</span>
                <span
                  className={`${styles["filter-toggle-icon"]} ${
                    showMobileFilters ? styles.open : ""
                  }`}
                >
                  <FaChevronDown />
                </span>
              </div>
            )}

            {isMobile && showMobileFilters && (
              <div className={styles["mobile-filters"]}>
                <button
                  className={styles["close-filters"]}
                  onClick={() => setShowMobileFilters(false)}
                >
                  <FaTimes />
                </button>
                <h2>Filters</h2>
                {renderFilters()}
              </div>
            )}

            <NovelSpinner
              selectedOption={sortOption}
              onSortOptionChange={handleSort}
            />
            {renderNovelsList()}
            <div className={styles["pagination-container"]}>
              <PaginationComponent
                currentPage={page}
                currentPageInput={pageInput}
                setCurrentPageInput={handlePageInputChange}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </div>
          </div>
          {!isMobile && (
            <div className={styles["right-content"]}>{renderFilters()}</div>
          )}
        </div>
      </div>
    </>
  );
};

export default HomePage;
