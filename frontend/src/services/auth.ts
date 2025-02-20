import { LoginResponse } from "../models/LoginResponse";
import { apiClient } from "./apiClient";

const ACCESS_TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

export const initiateRegistration = async (
    username: string,
    password: string,
    email: string
): Promise<void> => {
    try {
        await apiClient.post('/auth/register/initiate', { username, password, email }, { requireAuth: false });
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(error.message);
        }
        throw new Error('Có lỗi xảy ra');
    }
};

export const verifyOtpRegistration = async (
    otp: string,
    email: string,
): Promise<void> => {
    try {
        await apiClient.post('/auth/register/verify', { otp, email }, { requireAuth: false });
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(error.message);
        }
        throw new Error('Có lỗi xảy ra');
    }
};

export const initiatePasswordReset = async (
    email: string
): Promise<void> => {
    try {
        await apiClient.post('/auth/password/reset/initiate', { email }, { requireAuth: false });
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(error.message);
        }
        throw new Error('Có lỗi xảy ra');
    }
};

export const verifyOtpPasswordReset = async (
    otp: string,
    email: string,
    password: string
): Promise<void> => {
    try {
        await apiClient.post('/auth/password/reset/confirm', { otp, email, newPassword: password }, { requireAuth: false });
    } catch (error) {
        if (error instanceof Error) {
            throw new Error(error.message);
        }
        throw new Error('Có lỗi xảy ra');
    }
};

export const login = async (username: string, password: string): Promise<LoginResponse> => {
    return apiClient.post<LoginResponse>('/auth/login', { username, password }, { requireAuth: false });
};

export const loginWithGoogle = async (idToken: string): Promise<LoginResponse> => {
    return apiClient.post<LoginResponse>('/auth/login-google', { idToken }, { requireAuth: false });
};

export const refreshTokenAndGetUser = async (): Promise<LoginResponse> => {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
        throw new Error('No refresh token found');
    }

    const data = await apiClient.post<LoginResponse>('/auth/refresh', { refreshToken }, { requireAuth: false });

    const remember = !!localStorage.getItem(REFRESH_TOKEN_KEY);
    saveTokens(data.token, remember);

    return data;
};

export const saveTokens = (tokens: LoginResponse['token'], remember: boolean = false) => {
    sessionStorage.setItem(ACCESS_TOKEN_KEY, tokens.accessToken);

    if (remember) {
        localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
    } else {
        sessionStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
    }
};

export const getAccessToken = (): string | null => {
    return sessionStorage.getItem(ACCESS_TOKEN_KEY);
};

export const getRefreshToken = (): string | null => {
    return localStorage.getItem(REFRESH_TOKEN_KEY) || sessionStorage.getItem(REFRESH_TOKEN_KEY);
};

export const clearTokens = () => {
    sessionStorage.removeItem(ACCESS_TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
};
