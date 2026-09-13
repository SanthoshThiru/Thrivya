import axiosClient from "./axiosClient";

export async function getStudentDashboard() {
  const response = await axiosClient.get(
    "/student/dashboard"
  );

  return response.data;
}

export async function getPlacementDashboard() {
  const response = await axiosClient.get(
    "/placement/dashboard"
  );

  return response.data;
}

export async function getRecruiterDashboard() {
  const response = await axiosClient.get(
    "/recruiter/dashboard"
  );

  return response.data;
}

export async function getInterviewerDashboard() {
  const response = await axiosClient.get(
    "/interviewer/dashboard"
  );

  return response.data;
}