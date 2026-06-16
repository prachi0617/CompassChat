import axiosClient from "./axiosClient";

export const getReminders = async () => {
  const response = await axiosClient.get("/reminders");
  return response.data;
};

export const createReminder = async (data) => {
  const response = await axiosClient.post("/reminders", data);
  return response.data;
};

export const completeReminder = async (id) => {
  const response = await axiosClient.put(`/reminders/${id}/complete`);
  return response.data;
};
