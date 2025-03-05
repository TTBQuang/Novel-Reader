import { useState } from "react";
import { fetchFirebaseToken } from "../services/auth";
import { getStorage, ref, uploadBytes, listAll, deleteObject, getDownloadURL } from "firebase/storage";
import { getAuth, signInWithCustomToken } from "firebase/auth";
import { updateCoverImageUrl } from "../services/usersApi";

export const useUpdateCoverImage = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const uploadAndUpdateCoverImage = async (imageUrl: string, userId: number) => {
        setError(null);
        setLoading(true);

        try {
            // Get custom token from backend
            const response = await fetchFirebaseToken();
            const token = response.firebaseToken;

            // Sign in with custom token to authenticate
            const auth = getAuth();
            await signInWithCustomToken(auth, token);

            // Get Storage object from Firebase
            const storage = getStorage();

            // Tham chiếu đến thư mục covers/{userId}
            const userCoverFolderRef = ref(storage, `covers/${userId}`);

            // Delete all existing files (if any)
            const listResult = await listAll(userCoverFolderRef);
            const deletePromises = listResult.items.map((item) => deleteObject(item));
            await Promise.all(deletePromises);

            // Upload new file to Firebase Storage
            const imageResponse = await fetch(imageUrl);
            const blob = await imageResponse.blob();
            const fileName = imageUrl.split('/').pop()?.split('?')[0] || "file.png";
            const fileRef = ref(storage, `covers/${userId}/${fileName}`);
            await uploadBytes(fileRef, blob);

            // Get download URL of the uploaded file
            const downloadURL = await getDownloadURL(fileRef);

            await updateCoverImageUrl(downloadURL);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Đã có lỗi xảy ra');
        } finally {
            setLoading(false);
        }
    };

    return { uploadAndUpdateCoverImage, loading, error };
};