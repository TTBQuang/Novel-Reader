import { UsersResponse } from "../models/User";
import { apiClient } from "./apiClient";

export const fetchUsers = async (
    page: number,
    size: number,
    keyword: string = ""
): Promise<UsersResponse> => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        keyword: keyword
    });

    return apiClient.get<UsersResponse>(`/users?${params.toString()}`, { requireAuth: true });
};

export const updateCommentBlockedStatus = async (
    userId: number,
    isBlocked: boolean
): Promise<void> => {
    return apiClient.patch<void>(`/users/${userId}/comment-blocked?isBlocked=${isBlocked}`, {}, { requireAuth: true });
};
