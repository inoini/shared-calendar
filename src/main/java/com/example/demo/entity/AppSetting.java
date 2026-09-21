package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_setting")
public class AppSetting {

    @Id
    private Long id = 1L;

    private String farmName = "農業管理システム";
    private Double monthlyHarvestTargetKg = 1000.0;
    private Boolean notificationsEnabled = true;
    private String uiTheme = "green";
    private String layoutMode = "standard";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFarmName() { return farmName; }
    public void setFarmName(String farmName) { this.farmName = farmName; }
    public Double getMonthlyHarvestTargetKg() { return monthlyHarvestTargetKg; }
    public void setMonthlyHarvestTargetKg(Double monthlyHarvestTargetKg) { this.monthlyHarvestTargetKg = monthlyHarvestTargetKg; }
    public Boolean getNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }
    public String getUiTheme() { return uiTheme; }
    public void setUiTheme(String uiTheme) { this.uiTheme = uiTheme; }
    public String getLayoutMode() { return layoutMode; }
    public void setLayoutMode(String layoutMode) { this.layoutMode = layoutMode; }
}
