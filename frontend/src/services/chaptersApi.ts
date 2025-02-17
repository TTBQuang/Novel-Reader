import { Chapter } from "../models/Chapter";
import { apiClient } from "./apiClient";

export const fetchChapterDetail = (chapterId: number): Promise<Chapter> => {
    return apiClient.get<Chapter>(`/chapters/${chapterId}`, { requireAuth: false });
};
