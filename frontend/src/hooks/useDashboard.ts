import { useQuery } from '@tanstack/react-query';
import apiClient from '../lib/apiClient';
import { DashboardData } from '../types';

export const useDashboard = () => {
    return useQuery({
        queryKey: ['dashboard'],
        queryFn: async (): Promise<DashboardData> => {
            const response = await apiClient.get<DashboardData>('/api/dashboard/');
            return response.data;
        },
    });
};
