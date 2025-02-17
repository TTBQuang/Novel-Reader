import { PageInfo } from "./PageInfo";
import { User } from "./User";

export interface Comment {
    id: number;
    user: User;
    content: string;
    createdAt: string;
}

export interface CommentsResponse {
    content: Comment[];
    page: PageInfo;
}