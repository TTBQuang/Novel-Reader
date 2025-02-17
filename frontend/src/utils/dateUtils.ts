export const timeAgo = (datetime: string): string => {
    const now = new Date();
    const commentDate = new Date(datetime);

    const diffInSeconds = Math.floor(
        (now.getTime() - commentDate.getTime()) / 1000
    );
    const minutes = Math.floor(diffInSeconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const years = Math.floor(days / 365);

    if (years > 0) return `${years} năm trước`;
    if (days > 0) return `${days} ngày trước`;
    if (hours > 0) return `${hours} giờ trước`;
    if (minutes > 0) return `${minutes} phút trước`;
    return "Vừa xong";
};

export const formatDate = (datetime: string): string => {
    return new Date(datetime).toLocaleDateString("en-CA");
};