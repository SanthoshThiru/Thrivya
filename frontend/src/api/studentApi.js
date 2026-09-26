import axiosClient from "./axiosClient";

export async function saveManualSkills(skillsText) {
  const response = await axiosClient.post(
    "/student/profile/manual-skills",
    {
      skillsText,
    }
  );

  return response.data;
}

export async function uploadResume(file) {
  const formData = new FormData();

  formData.append("file", file);

  const response = await axiosClient.post(
    "/student/profile/resume",
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );

  return response.data;
}

export async function getStudentProfile() {
  const response = await axiosClient.get(
    "/student/profile"
  );

  return response.data;
}

export async function getResumeHistory() {
  const response = await axiosClient.get(
    "/student/profile/resume-history"
  );

  return response.data;
}

export async function getRoleSuggestions() {
  const response = await axiosClient.get(
    "/student/roles/suggestions"
  );

  return response.data;
}

export async function searchDesiredRole(desiredRole) {
  const response = await axiosClient.post(
    "/student/roles/desired",
    {
      desiredRole,
    }
  );

  return response.data;
}

export async function selectRole(roleId) {
  const response = await axiosClient.post(
    "/student/roles/select",
    {
      roleId,
    }
  );

  return response.data;
}

export async function generateATSReport() {
  const response = await axiosClient.post(
    "/student/ats/generate"
  );

  return response.data;
}

export async function getLatestATSReport() {
  const response = await axiosClient.get(
    "/student/ats/latest"
  );

  return response.data;
}

export async function getATSHistory() {
  const response = await axiosClient.get(
    "/student/ats/history"
  );

  return response.data;
}

export async function calculateSkillGaps() {
  const response = await axiosClient.post(
    "/student/skill-gaps/calculate"
  );

  return response.data;
}

export async function getSkillGaps() {
  const response = await axiosClient.get(
    "/student/skill-gaps"
  );

  return response.data;
}

export async function getSkillGapHistory() {
  const response = await axiosClient.get(
    "/student/skill-gaps/history"
  );

  return response.data;
}

export async function updateSkillProgress(progressData) {
  const response = await axiosClient.post(
    "/student/progress",
    progressData
  );

  return response.data;
}