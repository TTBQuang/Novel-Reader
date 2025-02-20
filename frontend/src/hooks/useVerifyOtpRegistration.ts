import { useState } from "react";
import { verifyOtpRegistration } from "../services/auth";

const useVerifyOtpRegistration = () => {
    const [isVerifying, setIsVerifying] = useState(false);

    const verifyOtp = async (otp: string, email: string): Promise<boolean> => {
        setIsVerifying(true);
        return verifyOtpRegistration(otp, email)
            .then(() => true)
            .finally(() => setIsVerifying(false));
    };

    return { verifyOtp, isVerifying };
};


export default useVerifyOtpRegistration;
