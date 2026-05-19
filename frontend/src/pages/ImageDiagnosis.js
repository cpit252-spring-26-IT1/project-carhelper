import React, { useState } from "react";
import { analyzeImage } from "../api/api";
import { useApp } from "../context/AppContext";
import ReportView from "../components/ReportView";

export default function ImageDiagnosis() {
  const { text, user, language } = useApp();
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [report, setReport] = useState(null);
  const [message, setMessage] = useState("");

  async function submit(event) {
    event.preventDefault();

    if (!file) {
      return;
    }

    setLoading(true);
    setMessage("");
    setReport(null);

    try {
      const data = await analyzeImage(file, user?.id, language);
      setReport(data);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="container narrow">
      <section className="panel">
        <h1>{text.imageDiagnosis}</h1>

        {!user && <p className="message">{text.loginRequired}</p>}

        <form onSubmit={submit} className="stack-form">
          <input type="file" accept="image/*" onChange={(e) => setFile(e.target.files[0])} required />

          <button className="main-button" disabled={loading}>
            {loading ? text.loading : text.analyze}
          </button>
        </form>

        {message && <p className="message error">{message}</p>}

        <ReportView title={report?.issueName || text.result} result={report} />
      </section>
    </main>
  );
}