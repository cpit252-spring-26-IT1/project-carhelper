import React from "react";
import { AppProvider } from "./context/AppContext";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";
import Profile from "./pages/Profile";
import ImageDiagnosis from "./pages/ImageDiagnosis";
import SymptomDescription from "./pages/SymptomDescription";
import RepairCostEstimator from "./pages/RepairCostEstimator";
import ResaleValueCalculator from "./pages/ResaleValueCalculator";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";
import "./style.css";

function AppContent() {
  const path = window.location.pathname;

  function goTo(page) {
    const cleanPage = page === "home" ? "" : page;
    window.location.href = `/${cleanPage}`;
  }

  function renderPage() {
    if (path === "/login") return <Login setPage={goTo} />;
    if (path === "/signup") return <SignUp setPage={goTo} />;
    if (path === "/profile") return <Profile setPage={goTo} />;
    if (path === "/image") return <ImageDiagnosis />;
    if (path === "/symptoms") return <SymptomDescription />;
    if (path === "/repair") return <RepairCostEstimator />;
    if (path === "/resale") return <ResaleValueCalculator />;
    if (path === "/forgot-password") return <ForgotPasswordPage setPage={goTo} />;

    return <Home setPage={goTo} />;
  }

  return (
    <>
      <Navbar setPage={goTo} />
      {renderPage()}
    </>
  );
}

export default function App() {
  return (
    <AppProvider>
      <AppContent />
    </AppProvider>
  );
}