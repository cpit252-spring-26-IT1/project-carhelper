import React, { useState } from "react";
import { resetPassword } from "../api/api";

export default function ForgotPasswordPage({ setPage }) {
  const [form, setForm] = useState({
    email: "",
    newPassword: ""
  });

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleResetPassword(e) {
    e.preventDefault();
    setMessage("");

    if (!form.email) {
      setMessage("Email is required");
      return;
    }

    if (form.newPassword.length < 8) {
      setMessage("Password must be at least 8 characters");
      return;
    }

    try {
      setLoading(true);
      const result = await resetPassword(form);
      setMessage(result.message || "Password reset successful");

      setForm({
        email: "",
        newPassword: ""
      });

      setTimeout(() => {
        setPage("login");
      }, 1200);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <h1>Forgot Password</h1>
        <p>Enter your email and new password to reset your account password.</p>

        <form onSubmit={handleResetPassword} className="auth-form">
          <input
            type="email"
            placeholder="Email"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
          />

          <input
            type="password"
            placeholder="New Password"
            value={form.newPassword}
            onChange={(e) => setForm({ ...form, newPassword: e.target.value })}
            required
          />

          <button type="submit" className="auth-submit" disabled={loading}>
            {loading ? "Resetting..." : "Reset Password"}
          </button>
        </form>

        {message && <div className="auth-message">{message}</div>}

        <div className="auth-switch">
          <span>Remember your password?</span>
          <button type="button" onClick={() => setPage("login")}>
            Back to Login
          </button>
        </div>
      </section>
    </main>
  );
}