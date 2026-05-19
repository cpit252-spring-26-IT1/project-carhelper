import React, { useEffect, useState } from "react";
import { getHistory } from "../api/api";
import { useApp } from "../context/AppContext";

export default function Profile() {
  const { text, user } = useApp();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    async function loadHistory() {
      if (!user?.id) {
        setMessage("Please login to view history.");
        setHistory([]);
        return;
      }

      setLoading(true);
      setMessage("");

      try {
        const data = await getHistory(user.id);
        setHistory(Array.isArray(data) ? data : []);
      } catch (error) {
        setMessage(error.message || "Could not load history.");
        setHistory([]);
      } finally {
        setLoading(false);
      }
    }

    loadHistory();
  }, [user?.id]);

  return (
    <main className="container">
      <section className="panel">
        <h1>{text.profile || "Profile"}</h1>

        {user ? (
          <p>
            {user.username} - {user.email}
          </p>
        ) : (
          <p className="message">Please login first.</p>
        )}

        <h2>{text.history || "History"}</h2>

        {loading && <p>Loading history...</p>}

        {message && <p className="message">{message}</p>}

        {!loading && !message && history.length === 0 && (
          <p>No history found.</p>
        )}

        <div className="history-list">
          {history.map((item) => (
            <div className="history-card" key={item.id}>
              <h3>{item.featureName}</h3>

              <p>
                <strong>Input:</strong> {item.inputText}
              </p>

              <p className="report-text">
                <strong>Result:</strong>
                {"\n"}
                {item.resultText}
              </p>

              {item.createdAt && (
                <small>{new Date(item.createdAt).toLocaleString()}</small>
              )}
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}