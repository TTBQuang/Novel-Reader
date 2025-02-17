import { PageInfo } from "./PageInfo";

export interface User {
    id: number;
    email: string;
    username: string;
    commentBlocked: boolean;
    admin: boolean;
}

export interface UsersResponse {
    content: User[];
    page: PageInfo;
}