import { useState } from "react";
import { updateDisplayName } from "../services/usersApi";

export const useUpdateDisplayName = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);

    const updateName = async (newName: string) => {
        setLoading(true);
        setError(null);
        setSuccess(false);

        try {
            await updateDisplayName(newName);
            setSuccess(true);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Đã có lỗi xảy ra');
        } finally {
            setLoading(false);
        }
    };

    return { updateName, loading, error, success };
};
