package com.carhelper.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_cost_records")
public class RepairCostRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carBrand;
    private String carModel;
    private String carYear;
    @Column(length = 2000)
    private String problemDescription;

    @Column(length = 6000)
    private String aiResult;

    private LocalDateTime createdAt;

    public RepairCostRecord() {
    }

    public RepairCostRecord(String carBrand, String carModel, String carYear, String problemDescription, String aiResult) {
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carYear = carYear;
        this.problemDescription = problemDescription;
        this.aiResult = aiResult;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getAiResult() {
        return aiResult;
    }

    public void setAiResult(String aiResult) {
        this.aiResult = aiResult;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}