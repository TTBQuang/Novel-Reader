import { User } from "./User";

export interface LoginResponse {
    token: {
        accessToken: string;
        refreshToken: string;
    };
    user: User;
}