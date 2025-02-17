import { useState, useEffect } from 'react';
import { getChapterComments } from '../services/commentsApi';
import { Comment } from '../models/Comment';

interface UseCommentsResult {
    comments: Comment[];
    totalComments: number;
    currentPage: number;
    totalPages: number;
    isLoading: boolean;
    error: Error | null;
    fetchComments: (page: number) => Promise<void>;
    addComment: (newComment: Comment) => void;
}

export const useChapterComments = (chapterId: number | null): UseCommentsResult => {
    const [comments, setComments] = useState<Comment[]>([]);
    const [totalComments, setTotalComments] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);

    const fetchComments = async (page: number = 0) => {
        if (!chapterId) return;

        setIsLoading(true);
        setError(null);

        try {
            const response = await getChapterComments(chapterId, page);
            setComments(response.content);
            setTotalComments(response.page.totalElements);
            setCurrentPage(response.page.number);
            setTotalPages(response.page.totalPages);
        } catch (err) {
            setError(err instanceof Error ? err : new Error('Failed to fetch comments'));
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
    }, [chapterId]);

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