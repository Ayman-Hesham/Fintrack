import React, { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { User, AuthState, AuthResponse } from '../types';
import apiClient from '../lib/apiClient';

interface AuthContextType extends AuthState {
  login: (email: string, password: string) => Promise<boolean>;
  register: (name: string, email: string, password: string) => Promise<boolean>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [authState, setAuthState] = useState<AuthState>({
    isAuthenticated: false,
    user: null,
    token: null
  });

  useEffect(() => {
    // Check for existing auth data in localStorage
    const storedAuth = localStorage.getItem('finance-auth');
    if (storedAuth) {
      try {
        const authData = JSON.parse(storedAuth);
        if (authData.isAuthenticated && authData.user && authData.token) {
          setAuthState(authData);
        }
      } catch {
        localStorage.removeItem('finance-auth');
      }
    }
  }, []);

  const login = useCallback(async (email: string, password: string): Promise<boolean> => {
    try {
      const response = await apiClient.post<AuthResponse>('/api/auth/login', {
        email,
        password
      });
      
      const { token, user } = response.data;
      
      const authData: AuthState = {
        isAuthenticated: true,
        user,
        token
      };
      
      setAuthState(authData);
      localStorage.setItem('finance-auth', JSON.stringify(authData));
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  }, []);

  const register = useCallback(async (name: string, email: string, password: string): Promise<boolean> => {
    try {
      // Register the user
      await apiClient.post<User>('/api/auth/register', {
        name,
        email,
        password
      });

      // After successful registration, log them in
      return await login(email, password);
    } catch (error) {
      console.error('Registration failed:', error);
      return false;
    }
  }, [login]);

  const logout = useCallback(() => {
    setAuthState({
      isAuthenticated: false,
      user: null,
      token: null
    });
    localStorage.removeItem('finance-auth');
  }, []);

  return (
    <AuthContext.Provider value={{ ...authState, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};