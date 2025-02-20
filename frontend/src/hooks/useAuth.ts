import { useContext } from "react";
import { UserContext } from "../context/UserContext";

export const useAuth = () => {
    const context = useContext(UserContext);
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};