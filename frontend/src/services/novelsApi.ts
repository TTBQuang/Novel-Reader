import { Novel } from "../models/Novel";
import { NovelsResponse } from "../models/NovelsResponse";
import { apiClient } from "./apiClient";

export const fetchNovels = (
    page: number,
    size: number,
    sortOption?: string,
    keyword?: string,
    status?: string,
    genreIds?: string
): Promise<NovelsResponse> => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });

    if (sortOption) params.append("sortOption", sortOption);
    if (keyword) params.append("keyword", keyword);
    if (status) params.append("status", status);
    if (genreIds) params.append("genreIds", genreIds);

    return apiClient.get<NovelsResponse>(`/novels?${params.toString()}`, { requireAuth: false });
};

export const fetchNovelDetail = (novelId: number): Promise<Novel> => {
    return apiClient.get<Novel>(`/novels/${novelId}`, { requireAuth: false });
};