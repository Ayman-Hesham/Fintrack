import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '../lib/apiClient';
import { Budget, CreateBudgetRequest } from '../types';

export const useBudgets = () => {
    return useQuery({
        queryKey: ['budgets'],
        queryFn: async (): Promise<Budget[]> => {
            const response = await apiClient.get<Budget[]>('/api/budgets/');
            return response.data;
        },
    });
};

export const useCreateBudget = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (data: CreateBudgetRequest): Promise<Budget> => {
            const response = await apiClient.post<Budget>('/api/budgets/', data);
            return response.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['budgets'] });
        },
    });
};

export const useUpdateBudget = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async ({ id, data }: { id: number; data: CreateBudgetRequest }): Promise<Budget> => {
            const response = await apiClient.put<Budget>(`/api/budgets/${id}`, data);
            return response.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['budgets'] });
        },
    });
};

export const useDeleteBudget = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (id: number): Promise<void> => {
            await apiClient.delete(`/api/budgets/${id}`);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['budgets'] });
        },
    });
};
