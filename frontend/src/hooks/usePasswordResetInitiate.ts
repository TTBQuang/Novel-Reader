import { useState } from 'react';
import { initiatePasswordReset } from '../services/auth';
import { toast } from 'react-toastify';

export const usePasswordResetInitiate = () => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const resetPassword = async (email: string): Promise<boolean> => {
        setIsLoading(true);
        setError(null);
        try {
            await initiatePasswordReset(email);
            return true;
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Đã có lỗi xảy ra';
            toast.error(message);
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    return { resetPassword, isLoading, error };
};
