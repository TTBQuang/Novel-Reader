import { Genre } from "../models/Genre";
import { apiClient } from "./apiClient";

export const fetchGenres = (): Promise<Genre[]> => {
    return apiClient.get<Genre[]>("/genres", { requireAuth: false });
};
