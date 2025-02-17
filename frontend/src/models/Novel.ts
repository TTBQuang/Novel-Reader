import { ChapterGroup } from "./ChapterGroup";
import { Genre } from "./Genre";

export interface Novel {
    id: number;
    poster: {
        id: number;
        email: string;
        username: string;
        commentBlocked: boolean;
        admin: boolean;
    };
    chapterGroups: ChapterGroup[];
    name: string;
    author: string;
    artist: string;
    cover: string;
    summary: string;
    status: string;
    wordsCount: number;
    lastUpdateDate: string;
    genres: Genre[];
}