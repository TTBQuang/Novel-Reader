import { User, UsersResponse } from "../models/User";
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

export const fetchUserDetail = async (userId: number): Promise<User> => {
    return apiClient.get<User>(`/users/${userId}`, { requireAuth: false });
};

export const updateDisplayName = async (newName: string): Promise<void> => {
    return apiClient.patch<void>(`/users/display-name`, { displayName: newName }, { requireAuth: true });
};

export const updateAvatarUrl = async (imageUrl: string): Promise<void> => {
    return apiClient.patch<void>(`/users/avatar`, { imageUrl }, { requireAuth: true });
}

export const updateCoverImageUrl = async (imageUrl: string): Promise<void> => {
    return apiClient.patch<void>(`/users/cover-image`, { imageUrl }, { requireAuth: true });
}
