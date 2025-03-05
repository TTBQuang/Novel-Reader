import { Novel } from "./Novel";
import { PageInfo } from "./PageInfo";

export interface User {
    id: number;
    email: string;
    username: string;
    displayName: string;
    commentBlocked: boolean;
    admin: boolean;
    avatar: string;
    createdAt: string;
    coverImage: string;
    chaptersCount: string;
    commentsCount: string;
    ownNovels: Novel[];
}

export interface UsersResponse {
    content: User[];
    page: PageInfo;
}