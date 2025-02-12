// api/novels.ts
import { NovelsResponse } from "../models/NovelItem";

export const fetchNovels = (
    page: number,
    size: number,
    sortOption?: string,
    keyword?: string,
    status?: string,
    genreIds?: string,
): Promise<NovelsResponse> => {
    let url = `http://localhost:8080/novels?page=${page}&size=${size}`;
    if (sortOption) {
        url += `&sortOption=${sortOption}`;
    }
    if (keyword) {
        url += `&keyword=${encodeURIComponent(keyword)}`;
    }
    if (status) {
        url += `&status=${status}`;
    }
    if (genreIds) {
        url += `&genreIds=${genreIds}`;
    }
    console.log(url);
    return fetch(url).then((res) => {
        if (!res.ok) {
            throw new Error("Network response was not ok");
        }
        return res.json();
    });
};
