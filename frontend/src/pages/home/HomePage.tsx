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

const getInitialSortOption = (
  urlSortOption: string | null
): NovelSortOption => {
  if (!urlSortOption) return NovelSortOption.AZ;
  return (
    Object.values(NovelSortOption).find(
      (option) => mapNovelSortOptionToApiValue(option) === urlSortOption
    ) || NovelSortOption.AZ
  );
};

const HomePage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const [filters, setFilters] = useState<FilterState>(() => ({
    ...DEFAULT_FILTERS,
    page: Number(searchParams.get("page")) || 1,
    pageInput: Number(searchParams.get("page")) || 1,
    sortOption: getInitialSortOption(searchParams.get("sortOption")),
    keyword: searchParams.get("keyword") || "",
  }));

  const { page, pageInput, sortOption, keyword, statusFilter, genreFilter } =
    filters;

  const updateUrlParams = useCallback(
    (newFilters: Partial<FilterState>, shouldReplace: boolean = false) => {
      const params = new URLSearchParams(searchParams);

      if (newFilters.page !== undefined) {
        params.set("page", newFilters.page.toString());
      }
      if (newFilters.sortOption !== undefined) {
        params.set(
          "sortOption",
          mapNovelSortOptionToApiValue(newFilters.sortOption)
        );
      }
      if (newFilters.keyword !== undefined) {
        if (newFilters.keyword.trim()) {
          params.set("keyword", newFilters.keyword);
        } else {
          params.delete("keyword");
        }
      }

      setSearchParams(params, { replace: shouldReplace });
    },
    [searchParams, setSearchParams]
  );

  useEffect(() => {
    const urlPage = Number(searchParams.get("page")) || 1;
    const urlSortOption = getInitialSortOption(searchParams.get("sortOption"));
    const urlKeyword = searchParams.get("keyword") || "";

    if (
      urlPage !== page ||
      urlSortOption !== sortOption ||
      urlKeyword !== keyword
    ) {
      setFilters((prev) => ({
        ...prev,
        page: urlPage,
        pageInput: urlPage,
        sortOption: urlSortOption,
        keyword: urlKeyword,
      }));
    }
  }, [keyword, page, searchParams, sortOption]);

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
      updateUrlParams(
        {
          sortOption: newSortOption,
          page: 1,
        },
        true
      );
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
        updateUrlParams({ page: newPage }, false);
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
      updateUrlParams(
        {
          keyword: newKeyword,
          page: 1,
        },
        true
      );
    },
    [updateUrlParams]
  );

  const handleStatusApply = useCallback((selectedStatus: NovelStatus[]) => {
    const statusParam = selectedStatus
      .map((status) => mapNovelStatusToApiValue(status))
      .join(",");

    setFilters((prev) => ({
      ...prev,
      statusFilter: statusParam,
      page: 1,
      pageInput: 1,
    }));
  }, []);

  const handleGenreApply = useCallback((selectedGenreIds: number[]) => {
    setFilters((prev) => ({
      ...prev,
      genreFilter: selectedGenreIds.join(","),
      page: 1,
      pageInput: 1,
    }));
  }, []);

  const handleLogoClick = useCallback(() => {
    setFilters(DEFAULT_FILTERS);
    setSearchParams({}, { replace: true });
  }, [setSearchParams]);

  const handlePageInputChange = useCallback((newPageInput: number) => {
    setFilters((prev) => ({ ...prev, pageInput: newPageInput }));
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

  return (
    <>
      <TopAppBar onSearch={handleSearch} onLogoClick={handleLogoClick} />
      <MainBanner />
      <div className={styles["home-page"]}>
        <div className={styles["home-page-content"]}>
          <div className={styles["left-content"]}>
            <NovelSpinner
              selectedOption={sortOption}
              onSortOptionChange={handleSort}
            />
            {renderNovelsList()}
            <div className={styles["pagination-container"]}>
              <PaginationComponent
                currentPageInput={pageInput}
                setCurrentPageInput={handlePageInputChange}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </div>
          </div>
          <div className={styles["right-content"]}>
            <StatusFilter onApply={handleStatusApply} />
            <GenreFilter genres={genresList} onApply={handleGenreApply} />
          </div>
        </div>
      </div>
    </>
  );
};

export default HomePage;
