import { CommentsResponse } from "../models/Comment";
import { Comment } from "../models/Comment";
import { apiClient } from "./apiClient";

export const getChapterComments = async (
    chapterId: number,
    page: number = 0,
    size: number = 10
): Promise<CommentsResponse> => {
    return apiClient.get<CommentsResponse>(
        `/comments/chapter/${chapterId}?page=${page}&size=${size}`,
        { requireAuth: false }
    );
};

export const getNovelComments = async (
    novelId: number,
    page: number = 0,
    size: number = 10
): Promise<CommentsResponse> => {
    return apiClient.get<CommentsResponse>(
        `/comments/novel/${novelId}?page=${page}&size=${size}`,
        { requireAuth: false }
    );
};

export const createComment = async (data: {
    novelId?: number;
    chapterId?: number;
    content: string;
}): Promise<Comment> => {
    if (data.novelId == null && data.chapterId == null) {
        throw new Error("Cần cung cấp novelId hoặc chapterId");
    }

    return apiClient.post<Comment>('/comments', data, { requireAuth: true });
};

export const deleteComment = async (commentId: number): Promise<void> => {
    return apiClient.delete<void>(`/comments/${commentId}`, { requireAuth: true });
}
