import React from "react";
import { Car, Sun, Moon } from "lucide-react";
import { useTranslation } from "react-i18next";
import { useApp } from "../context/AppContext";

export default function Navbar({ setPage }) {
  const { t } = useTranslation();
  const { user, logout, toggleTheme, theme, language, toggleLanguage } = useApp();

  function handleLogout() {
    logout();
    setPage("home");
  }

  return (
    <header className="navbar">
      <button className="brand" onClick={() => setPage("home")}>
        <Car size={32} />
        <span>{t("appName")}</span>
      </button>

      <nav>
        <button onClick={() => setPage("home")}>
          {t("home")}
        </button>

        {user ? (
          <>
            <button onClick={() => setPage("profile")}>
              {t("profile")}
            </button>

            <button onClick={handleLogout}>
              {t("logout")}
            </button>
          </>
        ) : (
          <>
            <button onClick={() => setPage("login")}>
              {t("login")}
            </button>

            <button
              className="signup-button"
              onClick={() => setPage("signup")}
            >
              {t("signup")}
            </button>
          </>
        )}

        <button className="circle-button" onClick={toggleLanguage}>
          {language === "en" ? "AR" : "EN"}
        </button>

        <button className="circle-button" onClick={toggleTheme}>
          {theme === "dark" ? <Sun size={20} /> : <Moon size={20} />}
        </button>
      </nav>
    </header>
  );
}