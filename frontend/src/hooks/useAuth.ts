import { useMutation } from '@tanstack/react-query';
import apiClient from '../lib/apiClient';
import { LoginRequest, RegisterRequest, AuthResponse, User } from '../types';

export const useLogin = () => {
    return useMutation({
        mutationFn: async (credentials: LoginRequest): Promise<AuthResponse> => {
            const response = await apiClient.post<AuthResponse>('/api/auth/login', credentials);
            return response.data;
        },
    });
};

export const useRegister = () => {
    return useMutation({
        mutationFn: async (userData: RegisterRequest): Promise<User> => {
            const response = await apiClient.post<User>('/api/auth/register', userData);
            return response.data;
        },
    });
};
