import { useState, useEffect } from "react";
import { fetchUsers } from "../services/usersApi";
import { UsersResponse } from "../models/User";

export const useUsers = (
    page: number,
    size: number,
    keyword: string = ""
) => {
    const [usersResponse, setUsersResponse] = useState<UsersResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string>("");

    useEffect(() => {
        setLoading(true);
        setError("");
        fetchUsers(page, size, keyword)
            .then((data) => setUsersResponse(data))
            .catch((err) =>
                setError(err.message || "Error fetching users")
            )
            .finally(() => setLoading(false));
    }, [page, size, keyword]);

    return { usersResponse, loading, error };
};
