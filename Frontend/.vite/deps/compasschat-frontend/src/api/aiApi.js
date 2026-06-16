import axiosClient from "./axiosClient";

export const sendAiMessage = async (message, userId = 1) => {
  const response = await axiosClient.post("/ai/chat", {
    message,
    userId,
  });

  return response.data;
};
