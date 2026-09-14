import { Navigate } from "react-router-dom";

export default function RoleRedirect() {
  const token = localStorage.getItem("thrivyaToken");
  const userString = localStorage.getItem("thrivyaUser");

  if (!token || !userString) {
    return <Navigate to="/home" replace />;
  }

  let user;

  try {
    user = JSON.parse(userString);
  } catch {
    localStorage.removeItem("thrivyaToken");
    localStorage.removeItem("thrivyaUser");

    return <Navigate to="/home" replace />;
  }

  const dashboardPaths = {
    STUDENT: "/student",
    PLACEMENT_CELL: "/placement",
    RECRUITER: "/recruiter",
    TECH_INTERVIEWER: "/interviewer",
  };

  const path = dashboardPaths[user.accountRole];

  if (!path) {
    localStorage.removeItem("thrivyaToken");
    localStorage.removeItem("thrivyaUser");

    return <Navigate to="/home" replace />;
  }

  return <Navigate to={path} replace />;
}