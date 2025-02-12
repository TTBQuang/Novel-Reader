import { Genre } from "../models/Genre";

export const fetchGenres = (): Promise<Genre[]> => {
    return fetch("http://localhost:8080/genres")
        .then((res) => {
            if (!res.ok) {
                throw new Error("Network response was not ok");
            }
            return res.json();
        });
};