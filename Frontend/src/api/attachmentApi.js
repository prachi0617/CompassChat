import axiosClient from "./axiosClient.js";

export async function uploadAttachment(messageId, file) {
    const formData = new FormData();
    formData.append("file", file);

    const response = await axiosClient.post(
        `/attachments/upload/${messageId}`,
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        }
    );

    return response.data;
}