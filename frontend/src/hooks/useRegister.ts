import { useState } from 'react';
import { register } from '../services/auth';
import { toast } from 'react-toastify';

interface RegisterForm {
    username: string;
    email: string;
    password: string;
}

interface FormErrors {
    email?: string;
    username?: string;
    password?: string;
}

export const useRegister = () => {
    const [formData, setFormData] = useState<RegisterForm>({
        username: '',
        email: '',
        password: '',
    });

    const [errors, setErrors] = useState<FormErrors>({});
    const [isLoading, setIsLoading] = useState(false);

    const validateEmail = (email: string): boolean => {
        const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
        return emailRegex.test(email);
    };

    const validateForm = (): boolean => {
        const newErrors: FormErrors = {};
        let isValid = true;

        if (formData.username.trim().length < 3) {
            newErrors.username = "Tên đăng nhập phải có ít nhất 3 ký tự";
            isValid = false;
        }

        if (!validateEmail(formData.email)) {
            newErrors.email = "Email không hợp lệ";
            isValid = false;
        }

        if (formData.password.length < 3) {
            newErrors.password = "Mật khẩu phải có ít nhất 3 ký tự";
            isValid = false;
        }

        setErrors(newErrors);
        return isValid;
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [id]: value
        }));

        setErrors(prev => ({
            ...prev,
            [id]: ''
        }));
    };

    const handleSubmit = async (): Promise<boolean> => {
        if (!validateForm()) {
            return false;
        }

        setIsLoading(true);
        try {
            return register(formData.username, formData.password, formData.email);
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Đã có lỗi xảy ra';
            toast.error(message);
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    return {
        formData,
        errors,
        isLoading,
        handleInputChange,
        handleSubmit
    };
};