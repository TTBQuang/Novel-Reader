import { useState } from 'react';
import { login } from '../services/auth';
import { toast } from 'react-toastify';
import { LoginResponse } from '../models/LoginResponse';

interface LoginForm {
    username: string;
    password: string;
}

interface FormErrors {
    username?: string;
    password?: string;
}

export const useLogin = () => {
    const [formData, setFormData] = useState<LoginForm>({
        username: '',
        password: '',
    });

    const [errors, setErrors] = useState<FormErrors>({});
    const [isLoading, setIsLoading] = useState(false);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [id]: value,
        }));

        setErrors(prev => ({
            ...prev,
            [id]: '',
        }));
    };

    const handleSubmit = async (): Promise<LoginResponse> => {
        setIsLoading(true);
        try {
            return await login(formData.username, formData.password);
        } catch (error) {
            const message =
                error instanceof Error ? error.message : 'Đã có lỗi xảy ra';
            toast.error(message);
            throw error;
        }
        finally {
            setIsLoading(false);
        }
    };

    return {
        formData,
        errors,
        isLoading,
        handleInputChange,
        handleSubmit,
    };
};
