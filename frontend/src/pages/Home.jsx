import React from "react";
import { Image, Car, Wrench } from "lucide-react";
import { useTranslation } from "react-i18next";

export default function Home({ setPage }) {
  const { t } = useTranslation();

  const services = [
    {
      title: t("imageAnalysis"),
      body: t("imageAnalysisDesc"),
      icon: <Image size={38} />,
      page: "image",
      color: "blue"
    },
    {
      title: t("carValuation"),
      body: t("carValuationDesc"),
      icon: <Car size={38} />,
      page: "resale",
      color: "purple"
    },
    {
      title: t("repairCost"),
      body: t("repairCostDesc"),
      icon: <Wrench size={38} />,
      page: "repair",
      color: "orange"
    }
  ];

  return (
    <main className="home-page">
      <section className="hero-section">
        <h1>
          {t("heroTitle1")}
          <span>{t("heroTitle2")}</span>
        </h1>
        <p>{t("heroDesc")}</p>
      </section>

      <section className="services-title">
        <h2>{t("services")}</h2>
        <p>{t("servicesDesc")}</p>
      </section>

      <section className="services-grid three-services">
        {services.map((service) => (
          <button
            key={service.page}
            className="service-card"
            onClick={() => setPage(service.page)}
          >
            <div className={`service-icon ${service.color}`}>
              {service.icon}
            </div>
            <h3>{service.title}</h3>
            <p>{service.body}</p>
          </button>
        ))}
      </section>

      <footer className="site-footer">
        {t("developedBy")}
      </footer>
    </main>
  );
}