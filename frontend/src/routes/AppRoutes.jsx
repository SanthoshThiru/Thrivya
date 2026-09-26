import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

// Public pages
import Home from "../pages/Home";
import Unauthorized from "../pages/Unauthorized";

// Authentication pages and guards
import Login from "../auth/Login";
import Register from "../auth/Register";
import ProtectedRoute from "../auth/ProtectedRoute";
import RoleRedirect from "../auth/RoleRedirect";

// Student pages
import StudentDashboard from "../pages/student/StudentDashboard";
import InputPreference from "../pages/student/InputPreference";
import StudentProfileSetup from "../pages/student/StudentProfileSetup";
import ManualSkills from "../pages/student/ManualSkills";
import ResumeUpload from "../pages/student/ResumeUpload";
import RoleSelection from "../pages/student/RoleSelection";
import MySkills from "../pages/student/MySkills";
import ATSReport from "../pages/student/ATSReport";
import SkillGaps from "../pages/student/SkillGaps";
import SkillGapHistory from "../pages/student/SkillGapHistory";
import ResumeVersions from "../pages/student/ResumeVersions";
import ProgressUpdate from "../pages/student/ProgressUpdate";

// Other role dashboards
import PlacementCellDashboard
  from "../pages/placement/PlacementCellDashboard";

import RecruiterDashboard
  from "../pages/recruiter/RecruiterDashboard";

import InterviewerDashboard
  from "../pages/interviewer/InterviewerDashboard";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Entry route */}
        <Route
          path="/"
          element={<RoleRedirect />}
        />

        {/* Public routes */}
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

        {/* =========================
            Student routes
        ========================== */}

        {/* Main student dashboard */}
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

        {/* Complete student profile setup flow */}
        <Route
          path="/student/setup"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <StudentProfileSetup />
            </ProtectedRoute>
          }
        />

        {/* Input preference page */}
        <Route
          path="/student/input-preference"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <InputPreference />
            </ProtectedRoute>
          }
        />

        {/* Manual skills page */}
        <Route
          path="/student/manual-skills"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <ManualSkills />
            </ProtectedRoute>
          }
        />

        {/* Resume upload page */}
        <Route
          path="/student/resume-upload"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <ResumeUpload />
            </ProtectedRoute>
          }
        />

        {/* Role selection page */}
        <Route
          path="/student/role-selection"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <RoleSelection />
            </ProtectedRoute>
          }
        />

        {/* Student skills and evidence */}
        <Route
          path="/student/skills"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <MySkills />
            </ProtectedRoute>
          }
        />

        {/* Explainable ATS report */}
        <Route
          path="/student/ats"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <ATSReport />
            </ProtectedRoute>
          }
        />

        {/* Ordered skill-gap analysis */}
        <Route
          path="/student/skill-gaps"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <SkillGaps />
            </ProtectedRoute>
          }
        />

        {/* Skill-gap history */}
        <Route
          path="/student/skill-gap-history"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <SkillGapHistory />
            </ProtectedRoute>
          }
        />

        {/* Resume version history */}
        <Route
          path="/student/resume-versions"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <ResumeVersions />
            </ProtectedRoute>
          }
        />

        {/* Manual learning progress update */}
        <Route
          path="/student/progress"
          element={
            <ProtectedRoute
              allowedRoles={["STUDENT"]}
            >
              <ProgressUpdate />
            </ProtectedRoute>
          }
        />

        {/* =========================
            Placement Cell routes
        ========================== */}

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

        {/* =========================
            Recruiter routes
        ========================== */}

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

        {/* =========================
            Technical Interviewer routes
        ========================== */}

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

        {/* Unauthorized route */}
        <Route
          path="/unauthorized"
          element={<Unauthorized />}
        />

        {/* Fallback route */}
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