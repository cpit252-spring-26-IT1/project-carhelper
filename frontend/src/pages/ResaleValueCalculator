import React, { useState } from "react";
import { estimateResaleValue } from "../api/api";
import { useApp } from "../context/AppContext";
import ReportView from "../components/ReportView";

export default function ResaleValueCalculator() {
  const { text, user, language } = useApp();
  const [form, setForm] = useState({
    brand: "",
    model: "",
    year: "",
    mileage: "",
    condition: "standard",
    mechanicalProblems: ""
  });
  const [result, setResult] = useState("");
  const [loading, setLoading] = useState(false);

  function update(name, value) {
    setForm({ ...form, [name]: value });
  }

  async function submit(event) {
    event.preventDefault();
    setLoading(true);
    setResult("");

    try {
      const data = await estimateResaleValue({
        ...form,
        userId: user?.id,
        year: Number(form.year),
        mileage: Number(form.mileage),
        language
      });

      setResult(data.result || data.message || data.resultText || data);
    } catch (error) {
      setResult(error.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="container narrow">
      <section className="panel">
        <h1>{text.resaleValue}</h1>

        {!user && <p className="message">{text.loginRequired}</p>}

        <form onSubmit={submit} className="grid-form">
          <input placeholder={text.brand} value={form.brand} onChange={(e) => update("brand", e.target.value)} required />
          <input placeholder={text.model} value={form.model} onChange={(e) => update("model", e.target.value)} required />
          <input type="number" placeholder={text.year} value={form.year} onChange={(e) => update("year", e.target.value)} required />
          <input type="number" placeholder={text.mileage} value={form.mileage} onChange={(e) => update("mileage", e.target.value)} required />

          <select value={form.condition} onChange={(e) => update("condition", e.target.value)}>
            <option value="standard">Standard</option>
            <option value="half full">Half Full</option>
            <option value="full">Full</option>
          </select>

          <textarea
            placeholder={text.mechanicalProblems}
            value={form.mechanicalProblems}
            onChange={(e) => update("mechanicalProblems", e.target.value)}
          />

          <button className="main-button full" disabled={loading}>
            {loading ? text.loading : text.submit}
          </button>
        </form>

        <ReportView title={text.result} result={result} />
      </section>
    </main>
  );
}