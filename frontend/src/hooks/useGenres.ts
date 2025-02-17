import { useState, useEffect } from "react";
import { Genre } from "../models/Genre";
import { fetchGenres } from "../services/genresApi";

export const useGenres = () => {
    const [genresList, setGenresList] = useState<Genre[]>([]);

    useEffect(() => {
        fetchGenres()
            .then((data: Genre[]) => {
                setGenresList(data);
            })
            .catch((err) => {
                console.error("Error fetching genres:", err);
            });
    }, []);

    return { genresList };
};
