import axiosClient from "./axiosClient";

export async function registerUser(registerData) {
  const response = await axiosClient.post(
    "/auth/register",
    registerData
  );

  return response.data;
}

export async function loginUser(loginData) {
  const response = await axiosClient.post(
    "/auth/login",
    loginData
  );

  return response.data;
}