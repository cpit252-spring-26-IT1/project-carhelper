import React, { useState } from "react";
import { loginUser } from "../api/api";
import { useApp } from "../context/AppContext";

export default function Login({ setPage }) {
  const { saveUser } = useApp();

  const [form, setForm] = useState({
    email: "",
    password: ""
  });

  const [message, setMessage] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();

    try {
      const user = await loginUser(form);
      saveUser(user);
      setMessage("Login successful");

      setTimeout(() => {
        window.location.href = "/";
      }, 1000);
    } catch (error) {
      setMessage(error.message);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <h1>Welcome Back</h1>
        <p>Login to continue using CarHelper</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <input
            type="email"
            placeholder="Email"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
          />

          <input
            type="password"
            placeholder="Password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
          />

          <button type="submit" className="auth-submit">
            Login
          </button>
        </form>

        <div className="forgot-password-link">
          <button type="button" onClick={() => setPage("forgot-password")}>
            Forgot Password?
          </button>
        </div>

        {message && <div className="auth-message">{message}</div>}

        <div className="auth-switch">
          <span>Don’t have an account?</span>
          <button type="button" onClick={() => setPage("signup")}>
            Sign Up
          </button>
        </div>
      </section>
    </main>
  );
}