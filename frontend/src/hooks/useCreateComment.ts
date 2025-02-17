import { useState, useCallback } from "react";
import { createComment as apiCreateComment } from "../services/commentsApi";
import { Comment } from "../models/Comment";

interface UseCreateCommentResult {
    createComment: (data: { novelId?: number; chapterId?: number; content: string }) => Promise<Comment | undefined>;
    isLoading: boolean;
    error: Error | null;
}

export const useCreateComment = (): UseCreateCommentResult => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);

    const createComment = useCallback(async (data: { novelId?: number; chapterId?: number; content: string }) => {
        setIsLoading(true);
        setError(null);
        try {
            const comment = await apiCreateComment(data);
            return comment;
        } catch (err) {
            setError(err instanceof Error ? err : new Error("Unknown error"));
        } finally {
            setIsLoading(false);
        }
    }, []);

    return { createComment, isLoading, error };
};
