import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import React, { createContext, ReactNode, useContext, useEffect, useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom';

type AuthContextType = {
    userId: number;
    setUserId: (id: number) => void;
    userToken: string;
    setUserToken: (token: string) => void;
    isProfileSetup: boolean;
    setIsProfileSetup: (isSetup: boolean) => void;
    username: string;
    setUsername: (name: string) => void;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    isAuthenticated: () => boolean;
    signup: (email: string, password: string, name: string) => Promise<void>;
  };

export const AuthContext = createContext<AuthContextType | null>(null);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
      throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
  };

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [userToken, setUserToken] = useState(''); // storing user's JWT token
    const [userId, setUserId] = useState(0); // storing user's ID
    const [isProfileSetup, setIsProfileSetup] = useState(false); // to check if user profile is set up
    const [username, setUsername] = useState(''); // storing user's name

    const navigate = useNavigate();

    useEffect(() => {
        const savedToken = localStorage.getItem('token');
        console.log('token: ', savedToken);
        setUserToken(savedToken || '');

        axios.get('http://localhost:8080/api/v1/user/me', {
            headers: {
                Authorization: `Bearer ${savedToken}`
            }
        })
        .then(response => {
            const data = response.data;
            console.log('user/me response:', data);
            setUserId(data.userId);
            setIsProfileSetup(data.profileSetup);
            setUsername(data.name);
            if (!data.profileSetup) {
                navigate('/profile-setup', { replace: true });
            }
            console.log('AuthProvider useEffect: userId fetched from API', data.userId, ' isProfileSetup:', data.profileSetup);
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
        })
    }, []);

    const login = async (email: string, password: string) => {
        try {
            const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
                email: email,
                password: password
            });
            const data = response.data;
            const token = data.token;
            setUserToken(token);
            localStorage.setItem('token', token);
            console.log('Login successful, token set:', token);
        } catch (error) {
            throw error; // rethrow the error to handle it in the component
        }
    };

    const logout = () => {
        setUserToken('');
        localStorage.removeItem('token');
    };

    const isAuthenticated = () => {
        return !!userToken; // returns true if token is not empty
    };

    const signup = async (email: string, password: string, name: string) => {
        try {
            const response = await axios.post('http://localhost:8080/api/v1/auth/signup', {

                email: email,
                password: password,
                name: name
            });
            const data = response.data;
            const token = data.token;
            setUserToken(token);
            localStorage.setItem('token', token);
            console.log('Sign up successful, token set:', token);
        } catch (error) {
            throw error; // rethrow the error to handle it in the component
        }
    }

    return (
        <AuthContext.Provider value={{ userId, userToken, setUserId, setUserToken, isProfileSetup, setIsProfileSetup, username, setUsername, login, logout, isAuthenticated, signup }}>
            {children}
        </AuthContext.Provider>
    );

}