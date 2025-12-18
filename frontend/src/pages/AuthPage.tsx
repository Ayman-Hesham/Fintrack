import React, { useState } from 'react';
import { TrendingUp } from 'lucide-react';
import { Card } from '../components/ui/Card';
import { LoginForm } from '../components/forms/LoginForm';
import { RegisterForm } from '../components/forms/RegisterForm';

export const AuthPage: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 flex items-center justify-center p-4">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <div className="flex justify-center items-center mb-4">
            <TrendingUp className="h-12 w-12 text-blue-600" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
            FinanceTracker
          </h1>
          <p className="mt-2 text-gray-600 dark:text-gray-400">
            Take control of your finances
          </p>
        </div>

        <Card>
          <div className="mb-6">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white text-center">
              {isLogin ? 'Welcome Back' : 'Get Started'}
            </h2>
            <p className="mt-2 text-center text-sm text-gray-600 dark:text-gray-400">
              {isLogin 
                ? 'Sign in to your account to continue'
                : 'Create a new account to start tracking'
              }
            </p>
          </div>

          {isLogin ? (
            <LoginForm onSwitchToRegister={() => setIsLogin(false)} />
          ) : (
            <RegisterForm onSwitchToLogin={() => setIsLogin(true)} />
          )}
        </Card>
      </div>
    </div>
  );
};