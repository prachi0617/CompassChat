import axiosClient from "./axiosClient";

export const getResources = async () => {
  const response = await axiosClient.get("/resources");
  return response.data;
};

export const searchResources = async (keyword) => {
  const response = await axiosClient.get(`/resources/search?keyword=${keyword}`);
  return response.data;
};
