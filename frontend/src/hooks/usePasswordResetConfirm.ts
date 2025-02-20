import { useState } from "react";
import { verifyOtpPasswordReset } from "../services/auth";

const usePasswordResetConfirm = () => {
    const [isVerifying, setIsVerifying] = useState(false);

    const verifyOtp = async (otp: string, email: string, password: string): Promise<boolean> => {
        setIsVerifying(true);
        return verifyOtpPasswordReset(otp, email, password)
            .then(() => true)
            .finally(() => setIsVerifying(false));
    };

    return { verifyOtp, isVerifying };
};


export default usePasswordResetConfirm;
