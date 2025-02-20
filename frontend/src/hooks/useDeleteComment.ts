import { useState } from "react";
import { deleteComment } from "../services/commentsApi";
import { toast } from "react-toastify";

export const useDeleteComment = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleDeleteComment = async (commentId: number) => {
        setLoading(true);
        setError(null);

        try {
            await deleteComment(commentId);
        } catch (err) {
            if (err instanceof Error) {
                toast.error(err.message);
            } else {
                toast.error("Đã xảy ra lỗi khi xóa bình luận");
            }
        } finally {
            setLoading(false);
        }
    };

    return { handleDeleteComment, loading, error };
};
