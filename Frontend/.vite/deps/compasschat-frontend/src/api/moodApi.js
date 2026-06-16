import axiosClient from "./axiosClient";

export const getMoods = async () => {
  const response = await axiosClient.get("/moods");
  return response.data;
};

export const createMood = async (data) => {
  const response = await axiosClient.post("/moods", data);
  return response.data;
};
