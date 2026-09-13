import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import Home from "../pages/Home";
import Unauthorized from "../pages/Unauthorized";

import Login from "../auth/Login";
import Register from "../auth/Register";
import ProtectedRoute from "../auth/ProtectedRoute";
import RoleRedirect from "../auth/RoleRedirect";

import StudentDashboard from "../pages/student/StudentDashboard";
import PlacementCellDashboard from "../pages/placement/PlacementCellDashboard";
import RecruiterDashboard from "../pages/recruiter/RecruiterDashboard";
import InterviewerDashboard from "../pages/interviewer/InterviewerDashboard";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={<RoleRedirect />}
        />

        <Route
          path="/home"
          element={<Home />}
        />

        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/register"
          element={<Register />}
        />

        <Route
          path="/student"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <StudentDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/placement"
          element={
            <ProtectedRoute
              allowedRoles={["PLACEMENT_CELL"]}
            >
              <PlacementCellDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/recruiter"
          element={
            <ProtectedRoute
              allowedRoles={["RECRUITER"]}
            >
              <RecruiterDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/interviewer"
          element={
            <ProtectedRoute
              allowedRoles={["TECH_INTERVIEWER"]}
            >
              <InterviewerDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/unauthorized"
          element={<Unauthorized />}
        />

        <Route
          path="*"
          element={
            <Navigate
              to="/home"
              replace
            />
          }
        />
      </Routes>
    </BrowserRouter>
  );
}