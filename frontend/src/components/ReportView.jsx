import React from "react";

export default function ReportView({ title, result }) {
  if (!result) {
    return null;
  }

  function cleanText(value) {
    if (!value) {
      return "";
    }

    return String(value)
      .replace(/\\n/g, "\n")
      .replace("###", "")
      .replace("##", "")
      .replace("#", "")
      .replace(/\*\*/g, "")
      .replace(/\*/g, "")
      .replace("---", "")
      .replace("S A R", "SAR")
      .trim();
  }

  function getText(value) {
    if (typeof value === "string") {
      return cleanText(value);
    }

    if (value.issueName || value.detectedProblems || value.repairSuggestion || value.estimatedCost || value.aiDisclaimer) {
      return {
        type: "diagnostic",
        issueName: cleanText(value.issueName),
        detectedProblems: cleanText(value.detectedProblems),
        repairSuggestion: cleanText(value.repairSuggestion),
        estimatedCost: cleanText(value.estimatedCost),
        aiDisclaimer: cleanText(value.aiDisclaimer)
      };
    }

    if (value.result) {
      return cleanText(value.result);
    }

    if (value.message) {
      return cleanText(value.message);
    }

    if (value.resultText) {
      return cleanText(value.resultText);
    }

    return cleanText(JSON.stringify(value, null, 2));
  }

  function formatResult(value) {
    return cleanText(value)
      .replace("Estimated Price:", "\nEstimated Price:")
      .replace("Estimated Repair Cost:", "\nEstimated Repair Cost:")
      .replace("Condition Summary:", "\n\nCondition Summary:")
      .replace("Problem Summary:", "\n\nProblem Summary:")
      .replace("Main Reasons:", "\n\nMain Reasons:")
      .replace("Possible Causes:", "\n\nPossible Causes:")
      .replace("1.", "\n1.")
      .replace("2.", "\n2.")
      .replace("3.", "\n3.")
      .replace("Advice:", "\n\nAdvice:")
      .replace("Recommendation:", "\n\nRecommendation:")
      .replace(/\n{3,}/g, "\n\n")
      .trim();
  }

  const content = getText(result);

  if (typeof content === "object" && content.type === "diagnostic") {
    return (
      <div className="report-box">
        <h2>{content.issueName || title}</h2>

        {content.detectedProblems && (
          <>
            <h3>Detected Problems</h3>
            <p className="report-text">{content.detectedProblems}</p>
          </>
        )}

        {content.repairSuggestion && (
          <>
            <h3>Repair Suggestion</h3>
            <p className="report-text">{content.repairSuggestion}</p>
          </>
        )}

        {content.estimatedCost && (
          <>
            <h3>Estimated Cost</h3>
            <p className="report-text">{content.estimatedCost}</p>
          </>
        )}

        {content.aiDisclaimer && (
          <>
            <h3>Disclaimer</h3>
            <p className="report-text">{content.aiDisclaimer}</p>
          </>
        )}
      </div>
    );
  }

  return (
    <div className="report-box">
      <h2>{title}</h2>
      <p className="report-text">{formatResult(content)}</p>
    </div>
  );
}