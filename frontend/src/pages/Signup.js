import React, { useState } from "react";
import { registerUser } from "../api/api";

export default function SignUp() {
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: ""
  });

  const [message, setMessage] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();

    try {
      await registerUser(form);
      setMessage("Account created successfully");

      setTimeout(() => {
        window.location.href = "/login";
      }, 1000);
    } catch (error) {
      setMessage(error.message);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <h1>Create Account</h1>
        <p>Join CarHelper today</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <input
            type="text"
            placeholder="Username"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            required
          />

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
            Sign Up
          </button>
        </form>

        {message && <div className="auth-message">{message}</div>}

        <div className="auth-switch">
          <span>Already have an account?</span>
          <button type="button" onClick={() => (window.location.href = "/login")}>
            Login
          </button>
        </div>
      </section>
    </main>
  );
}