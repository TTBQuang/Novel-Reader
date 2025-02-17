import { getAccessToken, refreshTokenAndGetUser } from './auth';

interface FetchOptions extends Omit<RequestInit, 'headers'> {
    requireAuth?: boolean;
    headers?: HeadersInit;
}

const BASE_URL = 'http://localhost:8080';

function normalizeHeaders(headers?: HeadersInit): Record<string, string> {
    const normalized: Record<string, string> = {};
    if (!headers) return normalized;

    if (headers instanceof Headers) {
        headers.forEach((value, key) => {
            normalized[key] = value;
        });
    } else if (Array.isArray(headers)) {
        for (const [key, value] of headers) {
            normalized[key] = value;
        }
    } else {
        Object.assign(normalized, headers);
    }
    return normalized;
}

async function getOrRefreshAccessToken(): Promise<string> {
    let token = getAccessToken();
    if (!token) {
        try {
            const response = await refreshTokenAndGetUser();
            token = response.token.accessToken;
        } catch {
            throw new Error('Unauthorized');
        }
    }
    return token;
}


async function callApi(endpoint: string, options: RequestInit, headers: Record<string, string>): Promise<Response> {
    return fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers,
    });
}

export const apiClient = {
    async fetch<T>(endpoint: string, options: FetchOptions = {}): Promise<T> {
        const { requireAuth = true, ...fetchOptions } = options;

        const headers: Record<string, string> = {
            'Content-Type': 'application/json',
            ...normalizeHeaders(fetchOptions.headers),
        };

        if (requireAuth) {
            try {
                headers['Authorization'] = `Bearer ${await getOrRefreshAccessToken()}`;
            } catch {
                throw new Error('Unauthorized');
            }
        }

        // First call to API
        let response = await callApi(endpoint, fetchOptions, headers);

        // Handle 401 Unauthorized error by refreshing token and retrying
        if (response.status === 401 && requireAuth) {
            try {
                const refreshResponse = await refreshTokenAndGetUser();
                headers['Authorization'] = `Bearer ${refreshResponse.token.accessToken}`;
                response = await callApi(endpoint, fetchOptions, headers);
            } catch {
                throw new Error('Unauthorized');
            }
        }

        // Handle 204 No Content response
        if (response.status === 204) {
            return {} as T;
        }

        // Handle other errors
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Có lỗi xảy ra');
        }

        // Parse JSON response
        const contentType = response.headers.get('Content-Type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        } else {
            return {} as T;
        }
    },

    get<T>(endpoint: string, options?: FetchOptions) {
        return this.fetch<T>(endpoint, { ...options, method: 'GET' });
    },

    post<T>(endpoint: string, data?: unknown, options?: FetchOptions) {
        return this.fetch<T>(endpoint, {
            ...options,
            method: 'POST',
            body: JSON.stringify(data),
        });
    },

    put<T>(endpoint: string, data?: unknown, options?: FetchOptions) {
        return this.fetch<T>(endpoint, {
            ...options,
            method: 'PUT',
            body: JSON.stringify(data),
        });
    },

    delete<T>(endpoint: string, options?: FetchOptions) {
        return this.fetch<T>(endpoint, { ...options, method: 'DELETE' });
    },

    patch<T>(endpoint: string, data?: unknown, options?: FetchOptions) {
        return this.fetch<T>(endpoint, {
            ...options,
            method: 'PATCH',
            body: JSON.stringify(data),
        });
    },
};
