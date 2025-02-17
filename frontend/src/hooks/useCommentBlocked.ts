import { useState } from "react";
import { updateCommentBlockedStatus } from "../services/usersApi";

export const useCommentBlocked = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const updateStatus = async (
        userId: number,
        isBlocked: boolean
    ): Promise<void> => {
        setLoading(true);
        setError(null);
        try {
            await updateCommentBlockedStatus(userId, !isBlocked);
            setLoading(false);
        } catch (err) {
            setError("Error updating comment status");
            setLoading(false);
            throw err;
        }
    };

    return { updateStatus, loading, error };
};
