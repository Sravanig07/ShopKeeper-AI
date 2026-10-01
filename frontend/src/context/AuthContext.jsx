import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('shelfiq_token'));
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('shelfiq_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (token && !user) {
      api.auth.getProfile()
        .then((profile) => {
          const userProfile = {
            id: profile.id || profile.userId,
            email: profile.email,
            fullName: profile.fullName,
            storeId: profile.activeStoreId || profile.storeId,
            storeCode: profile.storeCode || 'ST-METRO01',
            storeName: profile.activeStoreName || profile.storeName,
            roles: profile.roles,
          };
          setUser(userProfile);
          localStorage.setItem('shelfiq_user', JSON.stringify(userProfile));
        })
        .catch(() => logout());
    }
  }, [token]);

  const login = async (email, password) => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.auth.login(email, password);
      setToken(data.token);
      localStorage.setItem('shelfiq_token', data.token);

      const userProfile = {
        id: data.userId,
        email: data.email,
        fullName: data.fullName,
        storeId: data.storeId,
        storeCode: data.storeCode,
        storeName: data.storeName,
        roles: data.roles,
      };

      setUser(userProfile);
      localStorage.setItem('shelfiq_user', JSON.stringify(userProfile));
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const loginAsOwner = () => login('owner@shelfiq.io', 'Password123!');
  const loginAsStaff = () => login('staff@shelfiq.io', 'Password123!');

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('shelfiq_token');
    localStorage.removeItem('shelfiq_user');
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        isAuthenticated: !!token,
        loading,
        error,
        login,
        loginAsOwner,
        loginAsStaff,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
