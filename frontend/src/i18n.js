import i18n from "i18next";
import { initReactI18next } from "react-i18next";

const savedLanguage = localStorage.getItem("language") || "en";

const resources = {
  en: {
    translation: {
      appName: "CarHelper",
      home: "Home",
      login: "Login",
      signup: "Sign Up",
      logout: "Logout",
      profile: "Profile",
      heroTitle1: "Your Smart Assistant",
      heroTitle2: "for Car Inspection",
      heroDesc: "Diagnose car problems and calculate costs with the power of Artificial Intelligence.",
      services: "Our Services",
      servicesDesc: "Choose the service that fits your needs today",
      imageAnalysis: "Image Analysis",
      imageAnalysisDesc: "Upload car damage or warning light images and get an AI inspection report.",
      problemAnalysis: "Problem Analysis",
      problemAnalysisDesc: "Describe car symptoms and get an AI explanation with repair advice.",
      carValuation: "Car Valuation",
      carValuationDesc: "Estimate a fair market value based on car details and condition.",
      repairCost: "Repair Cost",
      repairCostDesc: "Estimate repair costs based on issue type and Saudi market prices.",
      developedBy: "Developed by Mohand & Abdulelah"
    }
  },
  ar: {
    translation: {
      appName: "CarHelper",
      home: "الرئيسية",
      login: "تسجيل الدخول",
      signup: "إنشاء حساب",
      logout: "تسجيل الخروج",
      profile: "الملف الشخصي",
      heroTitle1: "مساعدك الذكي",
      heroTitle2: "لفحص السيارات",
      heroDesc: "شخّص مشاكل السيارة واحسب التكاليف باستخدام الذكاء الاصطناعي.",
      services: "خدماتنا",
      servicesDesc: "اختر الخدمة المناسبة لك اليوم",
      imageAnalysis: "تحليل الصور",
      imageAnalysisDesc: "ارفع صورة ضرر أو لمبة تحذير واحصل على تقرير فحص ذكي.",
      problemAnalysis: "تحليل المشكلة",
      problemAnalysisDesc: "اكتب أعراض السيارة واحصل على تفسير ونصيحة إصلاح ذكية.",
      carValuation: "تقييم السيارة",
      carValuationDesc: "قدّر القيمة السوقية للسيارة حسب التفاصيل والحالة.",
      repairCost: "تكلفة الإصلاح",
      repairCostDesc: "قدّر تكلفة الإصلاح حسب نوع المشكلة وأسعار السوق السعودي.",
      developedBy: "تم التطوير بواسطة مهند وعبدالإله"
    }
  }
};

document.documentElement.dir = savedLanguage === "ar" ? "rtl" : "ltr";

i18n.use(initReactI18next).init({
  resources,
  lng: savedLanguage,
  fallbackLng: "en",
  interpolation: {
    escapeValue: false
  }
});

export default i18n;