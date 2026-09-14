import { Navigate, useLocation } from "react-router-dom";

export default function ProtectedRoute({
  children,
  allowedRoles,
}) {
  const location = useLocation();

  const token = localStorage.getItem("thrivyaToken");
  const userString = localStorage.getItem("thrivyaUser");

  if (!token || !userString) {
    return (
      <Navigate
        to="/login"
        state={{ from: location }}
        replace
      />
    );
  }

  let user;

  try {
    user = JSON.parse(userString);
  } catch {
    localStorage.removeItem("thrivyaToken");
    localStorage.removeItem("thrivyaUser");

    return <Navigate to="/login" replace />;
  }

  if (
    allowedRoles &&
    !allowedRoles.includes(user.accountRole)
  ) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
}