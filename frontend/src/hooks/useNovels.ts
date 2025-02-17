import { useState, useEffect } from "react";
import { NovelItem } from "../models/NovelItem";
import { fetchNovels } from "../services/novelsApi";
import { NovelsResponse } from "../models/NovelItem";
import { NOVELS_PER_PAGE } from "../utils/constants";

export const useNovels = (
    currentPage: number,
    size: number = NOVELS_PER_PAGE,
    sortOption?: string,
    keyword?: string,
    status?: string,
    genreIds?: string
) => {
    const [novelsList, setNovelsList] = useState<NovelItem[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [totalPages, setTotalPages] = useState<number>(10);

    useEffect(() => {
        setLoading(true);
        fetchNovels(currentPage, size, sortOption, keyword, status, genreIds)
            .then((data: NovelsResponse) => {
                setNovelsList(data.content);
                setTotalPages(data.page.totalPages);
                setLoading(false);
            })
            .catch((err) => {
                console.error("Error fetching novels:", err);
                setLoading(false);
            });
    }, [currentPage, size, sortOption, keyword, status, genreIds]);

    return { novelsList, loading, totalPages };
};
