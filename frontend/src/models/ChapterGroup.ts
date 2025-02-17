import { Chapter } from "./Chapter";

export interface ChapterGroup {
    id: number;
    name: string;
    groupOrder: number;
    chapters: Chapter[];
}