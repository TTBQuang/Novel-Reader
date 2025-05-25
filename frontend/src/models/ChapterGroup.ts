import { Chapter } from "./Chapter";

export interface ChapterGroup {
    id: number;
    name: string;
    image: string;
    groupOrder: number;
    chapters: Chapter[];
}