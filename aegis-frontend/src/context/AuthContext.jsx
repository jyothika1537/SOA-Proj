import React, { createContext, useState, useEffect } from 'react';
import { login as apiLogin, register as apiRegister } from '../services/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const role = localStorage.getItem('role');
        const email = localStorage.getItem('email');
        try {
            const payload = token ? JSON.parse(atob(token.split('.')[1])) : null;
            if (payload?.exp && payload.exp * 1000 <= Date.now()) throw new Error('expired');
            if (token) setUser({ email, role });
        } catch {
            localStorage.removeItem('token'); localStorage.removeItem('role'); localStorage.removeItem('email');
        }
        const unauthorized = () => setUser(null);
        window.addEventListener('auth:unauthorized', unauthorized);
        setLoading(false);
        return () => window.removeEventListener('auth:unauthorized', unauthorized);
    }, []);

    const login = async (email, password) => {
        try {
            const data = await apiLogin(email, password);
            localStorage.setItem('token', data.token);
            localStorage.setItem('role', data.role);
            localStorage.setItem('email', data.name || email);
            setUser({ email: data.name || email, role: data.role });
            return true;
        } catch (error) {
            console.error('Login error:', error);
            throw error;
        }
    };

    const register = async (userData) => {
        try {
            await apiRegister(userData);
            return true;
        } catch (error) {
            console.error('Register error:', error);
            throw error;
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('email');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
