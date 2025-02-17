import { useState, useEffect } from "react";
import { getNovelComments } from "../services/commentsApi";
import { Comment } from "../models/Comment";

interface UseNovelCommentsResult {
    comments: Comment[];
    totalComments: number;
    currentPage: number;
    totalPages: number;
    isLoading: boolean;
    error: Error | null;
    fetchComments: (page: number) => Promise<void>;
    addComment: (newComment: Comment) => void;
}

export const useNovelComments = (
    novelId: number | null
): UseNovelCommentsResult => {
    const [comments, setComments] = useState<Comment[]>([]);
    const [totalComments, setTotalComments] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);

    const fetchComments = async (page: number = 0) => {
        if (!novelId) return;
        setIsLoading(true);
        setError(null);
        try {
            const response = await getNovelComments(novelId, page);
            setComments(response.content);
            setTotalComments(response.page.totalElements);
            setCurrentPage(response.page.number);
            setTotalPages(response.page.totalPages);
        } catch (err) {
            setError(err instanceof Error ? err : new Error("Failed to fetch novel comments"));
        } finally {
            setIsLoading(false);
        }
    };

    const addComment = (newComment: Comment) => {
        setComments(prev => [newComment, ...prev]);
        setTotalComments(prev => prev + 1);
    };

    useEffect(() => {
        fetchComments(0);
    }, [novelId]);

    return {
        comments,
        totalComments,
        currentPage,
        totalPages,
        isLoading,
        error,
        fetchComments,
        addComment,
    };
};
