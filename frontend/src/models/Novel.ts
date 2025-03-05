import { ChapterGroup } from "./ChapterGroup";
import { Genre } from "./Genre";
import { User } from "./User";

export interface Novel {
    id: number;
    poster: User;
    chapterGroups: ChapterGroup[];
    name: string;
    author: string;
    artist: string;
    cover: string;
    summary: string;
    status: string;
    wordsCount: number;
    creationDate: string;
    lastUpdateDate: string;
    genres: Genre[];
}