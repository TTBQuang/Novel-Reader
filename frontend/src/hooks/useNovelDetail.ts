import { useEffect, useState } from "react";
import { fetchNovelDetail } from "../services/novelsApi";
import { Novel } from "../models/Novel";

export const useNovelDetail = (novelId: number | null) => {
    const [novel, setNovel] = useState<Novel | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        if (novelId === null) {
            console.error("ID không hợp lệ");
            setIsLoading(false);
            return;
        }

        setIsLoading(true);
        fetchNovelDetail(novelId)
            .then((data) => {
                setNovel(data);
                setIsLoading(false);
            })
            .catch((err) => {
                console.error("Error fetching novel data:", err);
                setError(err);
                setIsLoading(false);
            });
    }, [novelId]);

    return { novel, isLoading, error };
};
