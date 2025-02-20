import React, { useState, useEffect } from "react";
import {
  getAccessToken,
  getRefreshToken,
  clearTokens,
  refreshTokenAndGetUser,
} from "../services/auth";
import { UserContext, UserContextType } from "./UserContext";
import { User } from "../models/User";

export const UserProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const isAuthenticated = !!user && !!getAccessToken();

  const logout = () => {
    setUser(null);
    clearTokens();
  };

  useEffect(() => {
    const restoreSession = async () => {
      try {
        const refreshToken = getRefreshToken();
        if (!refreshToken) {
          setIsLoading(false);
          return;
        }

        const response = await refreshTokenAndGetUser();
        setUser(response.user);
      } catch (error) {
        console.error("Failed to restore session:", error);
        logout();
      } finally {
        setIsLoading(false);
      }
    };

    restoreSession();
  }, []);

  const contextValue: UserContextType = {
    user,
    setUser,
    isAuthenticated,
    logout,
    isLoading,
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <UserContext.Provider value={contextValue}>{children}</UserContext.Provider>
  );
};
