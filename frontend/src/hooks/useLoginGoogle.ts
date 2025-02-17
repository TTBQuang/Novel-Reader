import { useState, useCallback } from "react";
import { loginWithGoogle } from "../services/auth";
import { LoginResponse } from "../models/LoginResponse";

export const useLoginGoogle = () => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);

    const loginGoogle = useCallback(
        async (idToken: string): Promise<LoginResponse | undefined> => {
            setIsLoading(true);
            setError(null);
            try {
                const result = await loginWithGoogle(idToken);
                return result;
            } catch (err) {
                setError(err instanceof Error ? err : new Error("Unknown error"));
                return undefined;
            } finally {
                setIsLoading(false);
            }
        },
        []
    );

    return { loginGoogle, isLoading, error };
};
