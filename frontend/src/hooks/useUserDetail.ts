import { useState, useEffect } from "react";
import { fetchUserDetail } from "../services/usersApi";
import { User } from "../models/User";

export const useUserDetail = (userId: number) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string>("");

    useEffect(() => {
        if (!userId) return;
        setLoading(true);
        setError("");
        fetchUserDetail(userId)
            .then(setUser)
            .catch((err) =>
                setError(err.message || "Error fetching user details")
            )
            .finally(() => setLoading(false));
    }, [userId]);

    return { user, setUser, loading, error };
};