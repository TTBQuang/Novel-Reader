import { useEffect, useState } from "react";
import { fetchChapterDetail } from "../services/chaptersApi";
import { Chapter } from "../models/Chapter";

export const useChapterDetail = (chapterId: number) => {
    const [chapterData, setChapterData] = useState<Chapter | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        if (chapterId === null) {
            console.error("ID chương không hợp lệ");
            setIsLoading(false);
            return;
        }

        setIsLoading(true);
        fetchChapterDetail(chapterId)
            .then((data) => {
                setChapterData(data);
                setIsLoading(false);
            })
            .catch((err) => {
                console.error("Error fetching chapter data:", err);
                setError(err);
                setIsLoading(false);
            });
    }, [chapterId]);

    return { chapterData, isLoading, error };
};
