import React, { createContext, useContext, useEffect, useState } from "react";
import i18n from "../i18n";

const AppContext = createContext();

const englishText = {
  home: "Home",
  login: "Login",
  signup: "Sign Up",
  logout: "Logout",
  profile: "Profile",
  appName: "CarHelper",
  heroTitle: "CarHelper",
  heroText: "A smart web app for car diagnosis and pricing.",
  team: "CPIT252 - Software Design Patterns",
  imageDiagnosis: "Image Analysis",
  imageText: "Upload car damage or warning light images and get an AI inspection report.",
  problemAnalysis: "Problem Analysis",
  problemText: "Describe car symptoms and get an AI explanation with repair advice.",
  repairCost: "Repair Cost",
  repairText: "Estimate repair costs based on issue type and Saudi market prices.",
  resaleValue: "Car Valuation",
  resaleText: "Estimate a fair market value based on car details and condition.",
  username: "Username",
  email: "Email",
  password: "Password",
  submit: "Submit",
  analyze: "Analyze",
  loading: "Loading...",
  result: "Result",
  brand: "Brand",
  model: "Model",
  year: "Year",
  mileage: "Mileage",
  condition: "Condition",
  mechanicalProblems: "Mechanical Problems",
  describeSymptoms: "Describe the symptoms or problem",
  loginRequired: "Login is recommended so your result can be saved.",
  history: "History",
  noHistory: "No history found.",
  input: "Input",
  low: "Low",
  medium: "Medium",
  high: "High",
  engine: "Engine",
  paintBody: "Paint / Body",
  electrical: "Electrical",
  general: "General",
  issueName: "Issue Name",
  detectedProblems: "Detected Problems",
  repairSuggestion: "Repair Suggestion",
  estimatedCost: "Estimated Cost",
  aiDisclaimer: "Disclaimer"
};

const arabicText = {
  home: "الرئيسية",
  login: "تسجيل الدخول",
  signup: "إنشاء حساب",
  logout: "تسجيل الخروج",
  profile: "الملف الشخصي",
  appName: "CarHelper",
  heroTitle: "CarHelper",
  heroText: "تطبيق ذكي لتشخيص مشاكل السيارات وتقدير الأسعار.",
  team: "CPIT252 - أنماط تصميم البرمجيات",
  imageDiagnosis: "تحليل الصور",
  imageText: "ارفع صورة ضرر أو لمبة تحذير واحصل على تقرير فحص ذكي.",
  problemAnalysis: "تحليل المشكلة",
  problemText: "اكتب أعراض السيارة واحصل على تفسير ونصيحة إصلاح بالذكاء الاصطناعي.",
  repairCost: "تكلفة الإصلاح",
  repairText: "قدّر تكلفة الإصلاح حسب نوع المشكلة وأسعار السوق السعودي.",
  resaleValue: "تقييم السيارة",
  resaleText: "قدّر القيمة السوقية العادلة حسب تفاصيل السيارة وحالتها.",
  username: "اسم المستخدم",
  email: "البريد الإلكتروني",
  password: "كلمة المرور",
  submit: "إرسال",
  analyze: "تحليل",
  loading: "جاري التحميل...",
  result: "النتيجة",
  brand: "الشركة",
  model: "الموديل",
  year: "السنة",
  mileage: "الممشى",
  condition: "الحالة",
  mechanicalProblems: "المشاكل الميكانيكية",
  describeSymptoms: "اكتب الأعراض أو المشكلة",
  loginRequired: "يفضل تسجيل الدخول حتى يتم حفظ النتيجة.",
  history: "السجل",
  noHistory: "لا يوجد سجل.",
  input: "المدخلات",
  low: "منخفضة",
  medium: "متوسطة",
  high: "ممتازة",
  engine: "المحرك",
  paintBody: "البوية / الهيكل",
  electrical: "الكهرباء",
  general: "عام",
  issueName: "اسم المشكلة",
  detectedProblems: "المشاكل المكتشفة",
  repairSuggestion: "اقتراح الإصلاح",
  estimatedCost: "التكلفة التقديرية",
  aiDisclaimer: "تنبيه"
};

export function AppProvider({ children }) {
  const [theme, setTheme] = useState(localStorage.getItem("theme") || "dark");
  const [language, setLanguageState] = useState(localStorage.getItem("language") || "en");
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user");
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const text = language === "ar" ? arabicText : englishText;

  useEffect(() => {
    document.body.className = theme;
    localStorage.setItem("theme", theme);
  }, [theme]);

  useEffect(() => {
    document.documentElement.dir = language === "ar" ? "rtl" : "ltr";
    document.body.dir = language === "ar" ? "rtl" : "ltr";
    localStorage.setItem("language", language);
    i18n.changeLanguage(language);
  }, [language]);

  function toggleTheme() {
    setTheme(theme === "dark" ? "light" : "dark");
  }

  function setLanguage(newLanguage) {
    setLanguageState(newLanguage === "ar" ? "ar" : "en");
  }

  function toggleLanguage() {
    setLanguage(language === "en" ? "ar" : "en");
  }

  function saveUser(newUser) {
    setUser(newUser);
    localStorage.setItem("user", JSON.stringify(newUser));
  }

  function logout() {
    setUser(null);
    localStorage.removeItem("user");
  }

  return (
    <AppContext.Provider
      value={{
        theme,
        language,
        user,
        text,
        toggleTheme,
        toggleLanguage,
        setLanguage,
        saveUser,
        logout
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  return useContext(AppContext);
}