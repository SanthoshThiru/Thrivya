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
import StudentProfileSetup from "../pages/student/StudentProfileSetup";
import ResumeUpload from "../pages/student/ResumeUpload";
import ManualSkills from "../pages/student/ManualSkills";
import RoleSelection from "../pages/student/RoleSelection";
import MySkills from "../pages/student/MySkills";
import ATSReport from "../pages/student/ATSReport";
import SkillGaps from "../pages/student/SkillGaps";
import SkillGapHistory from "../pages/student/SkillGapHistory";
import ResumeVersions from "../pages/student/ResumeVersions";
import ProgressUpdate from "../pages/student/ProgressUpdate";
import ProjectEvidence from "../pages/student/ProjectEvidence";
import ResumeContent from "../pages/student/ResumeContent";

import PlacementCellDashboard from "../pages/placement/PlacementCellDashboard";
import RecruiterDashboard from "../pages/recruiter/RecruiterDashboard";
import InterviewerDashboard from "../pages/interviewer/InterviewerDashboard";

function StudentRoute({ children }) {
  return (
    <ProtectedRoute allowedRoles={["STUDENT"]}>
      {children}
    </ProtectedRoute>
  );
}

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Root route: redirects logged-in users by role */}
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

        {/* Student dashboard */}
        <Route
          path="/student"
          element={
            <StudentRoute>
              <StudentDashboard />
            </StudentRoute>
          }
        />

        {/* Student career-profile setup */}
        <Route
          path="/student/setup"
          element={
            <StudentRoute>
              <StudentProfileSetup />
            </StudentRoute>
          }
        />

        <Route
          path="/student/resume-upload"
          element={
            <StudentRoute>
              <ResumeUpload />
            </StudentRoute>
          }
        />

        <Route
          path="/student/manual-skills"
          element={
            <StudentRoute>
              <ManualSkills />
            </StudentRoute>
          }
        />

        <Route
          path="/student/role-selection"
          element={
            <StudentRoute>
              <RoleSelection />
            </StudentRoute>
          }
        />

        {/* Student profile and analysis */}
        <Route
          path="/student/skills"
          element={
            <StudentRoute>
              <MySkills />
            </StudentRoute>
          }
        />

        <Route
          path="/student/ats"
          element={
            <StudentRoute>
              <ATSReport />
            </StudentRoute>
          }
        />

        <Route
          path="/student/skill-gaps"
          element={
            <StudentRoute>
              <SkillGaps />
            </StudentRoute>
          }
        />

        <Route
          path="/student/skill-gap-history"
          element={
            <StudentRoute>
              <SkillGapHistory />
            </StudentRoute>
          }
        />

        {/* Student progress and resume improvement */}
        <Route
          path="/student/progress"
          element={
            <StudentRoute>
              <ProgressUpdate />
            </StudentRoute>
          }
        />

        <Route
          path="/student/resume-versions"
          element={
            <StudentRoute>
              <ResumeVersions />
            </StudentRoute>
          }
        />

        <Route
          path="/student/project-evidence"
          element={
            <StudentRoute>
              <ProjectEvidence />
            </StudentRoute>
          }
        />

        {/* Evidence-aware generated resume content */}
        <Route
          path="/student/resume-content"
          element={
            <StudentRoute>
              <ResumeContent />
            </StudentRoute>
          }
        />

        {/* Placement cell routes */}
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

        {/* Recruiter routes */}
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

        {/* Technical interviewer routes */}
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

        {/* Unknown paths */}
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