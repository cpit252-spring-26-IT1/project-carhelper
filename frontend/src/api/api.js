const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";

function cleanErrorMessage(errorText) {
  if (!errorText) {
    return "Something went wrong. Please try again.";
  }

  const text = String(errorText);

  if (text.includes("503") || text.toLowerCase().includes("unavailable")) {
    return "AI service is busy now. Please try again in a few minutes.";
  }

  if (text.includes("401") || text.includes("403") || text.toLowerCase().includes("api key")) {
    return "AI API key is missing or invalid. Please check backend settings.";
  }

  if (text.includes("429") || text.toLowerCase().includes("quota") || text.toLowerCase().includes("rate limit")) {
    return "AI request limit reached. Please try again later.";
  }

  if (text.toLowerCase().includes("failed to fetch")) {
    return "Cannot connect to backend. Make sure Spring Boot is running on port 8080.";
  }

  if (text.toLowerCase().includes("network")) {
    return "Network error. Please check your connection and try again.";
  }

  return text;
}

async function readResponse(response) {
  const text = await response.text();

  if (!text) {
    return {};
  }

  try {
    return JSON.parse(text);
  } catch (error) {
    return { message: text };
  }
}

async function request(path, options = {}) {
  const response = await fetch(API_BASE_URL + path, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  const data = await readResponse(response);

  if (!response.ok) {
    throw new Error(cleanErrorMessage(data.message || data.error || "Request failed"));
  }

  return data;
}

export async function registerUser(data) {
  return request("/api/auth/register", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

export async function loginUser(data) {
  return request("/api/auth/login", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

export async function analyzeImage(file, userId, language) {
  const formData = new FormData();
  formData.append("file", file);

  if (userId) {
    formData.append("userId", userId);
  }

  if (language) {
    formData.append("language", language);
  }

  const response = await fetch(API_BASE_URL + "/api/diagnosis/image", {
    method: "POST",
    body: formData
  });

  const data = await readResponse(response);

  if (!response.ok) {
    throw new Error(cleanErrorMessage(data.message || "Image analysis failed"));
  }

  return data;
}

export async function analyzeSymptoms(data) {
  return request("/api/diagnosis/symptoms", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

export async function estimateRepairCost(data) {
  return request("/api/cost/repair", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

export async function estimateResaleValue(data) {
  return request("/api/cost/resale", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

export async function getHistory(userId) {
  return request("/api/profile/history/" + userId);
}

export async function getUserHistory(userId) {
  return request("/api/profile/history/" + userId);
}