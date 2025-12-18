import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import apiClient from '../lib/apiClient';
import { Transaction, CreateTransactionRequest, Page, TransactionFilterParams } from '../types';

interface UseTransactionsParams {
    page?: number;
    size?: number;
}

export const useTransactions = (params: UseTransactionsParams = {}) => {
    const { page = 0, size = 20 } = params;

    return useQuery({
        queryKey: ['transactions', { page, size }],
        queryFn: async (): Promise<Page<Transaction>> => {
            const response = await apiClient.get<Page<Transaction>>('/api/transactions/', {
                params: { page, size }
            });
            return response.data;
        },
        placeholderData: keepPreviousData,
    });
};

export const useSearchTransactions = (query: string, page = 0, size = 20) => {
    return useQuery({
        queryKey: ['transactions', 'search', { query, page, size }],
        queryFn: async (): Promise<Page<Transaction>> => {
            const response = await apiClient.get<Page<Transaction>>('/api/transactions/search', {
                params: { query, page, size }
            });
            return response.data;
        },
        enabled: query.length > 0,
        placeholderData: keepPreviousData,
    });
};

export const useFilterTransactions = (filters: TransactionFilterParams) => {
    const { page = 0, size = 20, ...restFilters } = filters;

    return useQuery({
        queryKey: ['transactions', 'filter', filters],
        queryFn: async (): Promise<Page<Transaction>> => {
            const response = await apiClient.get<Page<Transaction>>('/api/transactions/filter', {
                params: { page, size, ...restFilters }
            });
            return response.data;
        },
        placeholderData: keepPreviousData,
    });
};

export const useCreateTransaction = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (data: CreateTransactionRequest): Promise<Transaction> => {
            const response = await apiClient.post<Transaction>('/api/transactions/', data);
            return response.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['transactions'] });
            queryClient.invalidateQueries({ queryKey: ['dashboard'] });
            queryClient.invalidateQueries({ queryKey: ['budgets'] });
        },
    });
};
